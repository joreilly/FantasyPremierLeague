import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.format.csvSafe
import dev.johnoreilly.common.format.playersAsCsv
import dev.johnoreilly.common.format.toFixed
import dev.johnoreilly.common.format.teamShortNames
import dev.johnoreilly.common.model.GameFixture
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonObject


private val koin = initKoin(enableNetworkLogs = false).koin

private fun text(body: String) = CallToolResult(content = listOf(TextContent(body)))

private fun intArg(request: CallToolRequest, name: String): Int? =
    request.arguments?.get(name)?.jsonPrimitive?.content?.toIntOrNull()

private fun optionalIntSchema(name: String, description: String) = ToolSchema(
    properties = buildJsonObject {
        putJsonObject(name) {
            put("type", JsonPrimitive("integer"))
            put("description", JsonPrimitive(description))
        }
    }
)

fun configureServer(): Server {
    val repository = koin.get<FantasyPremierLeagueRepository>()

    val server = Server(
        Implementation(
            name = "FantasyPremierLeague MCP Server",
            version = "1.0.0"
        ),
        ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = true)
            )
        )
    )

    server.addTool(
        name = "get-players",
        description = "Every FPL player with the stats needed to pick a squad: price, total points, " +
            "recent form, minutes played, points per game, ownership, expected points for the next " +
            "gameweek, expected goal involvements per 90, bonus, clean sheets, set-piece duty, " +
            "availability status and injury news. Returns compact CSV.",
        inputSchema = optionalIntSchema(
            "minMinutes",
            "Only include players who have played at least this many minutes this season. " +
                "Use 1 to exclude players who have never featured; omit to get the full pool."
        )
    ) { request ->
        text(repository.playersAsCsv(minMinutes = intArg(request, "minMinutes") ?: 0))
    }

    server.addTool(
        name = "get-fixture-difficulty",
        description = "Upcoming fixtures for every team with FPL's difficulty rating (1 = easiest, " +
            "5 = hardest), starting from the next gameweek, plus a per-team summary of the run. " +
            "Use this to judge which teams have a good or bad set of fixtures coming up.",
        inputSchema = optionalIntSchema(
            "gameweeks",
            "How many gameweeks ahead to include. Defaults to 5."
        )
    ) { request ->
        repository.awaitInitialLoad()
        val window = (intArg(request, "gameweeks") ?: 5).coerceIn(1, 38)
        val from = repository.nextGameweek.value
        val until = from + window
        val shortNames = repository.teamShortNames()

        val fixtures = repository.getFixtures().first().filter { it.event in from until until }

        data class Entry(val team: String, val gameweek: Int, val opponent: String, val home: Boolean, val difficulty: Int)

        val entries = fixtures.flatMap { f: GameFixture ->
            val home = shortNames[f.homeTeamId] ?: f.homeTeam
            val away = shortNames[f.awayTeamId] ?: f.awayTeam
            listOf(
                Entry(home, f.event, away, true, f.homeDifficulty),
                Entry(away, f.event, home, false, f.awayDifficulty)
            )
        }.sortedWith(compareBy({ it.team }, { it.gameweek }))

        val rows = entries.map {
            "${csvSafe(it.team)},${it.gameweek},${csvSafe(it.opponent)},${if (it.home) "H" else "A"},${it.difficulty}"
        }

        // Summing difficulty here rather than leaving 100+ rows of mental arithmetic to the caller.
        // A team with a blank gameweek simply has fewer fixtures, which the count column exposes.
        val summary = entries.groupBy { it.team }
            .map { (team, teamEntries) ->
                val total = teamEntries.sumOf { it.difficulty }
                Triple(team, teamEntries.size, total)
            }
            .sortedWith(compareBy({ it.third.toDouble() / it.second.coerceAtLeast(1) }, { -it.second }))
            .map { (team, count, total) ->
                "${csvSafe(team)},$count,$total,${(total.toDouble() / count.coerceAtLeast(1)).toFixed(2)}"
            }

        val header = listOf(
            "Fixture difficulty for gameweeks $from to ${until - 1}. fdr is 1 (easiest) to 5 (hardest).",
            "ha is H for home, A for away. A team with fewer rows than $window has a blank gameweek; " +
                "a team with more has a double gameweek.",
            "",
            "team,gw,opp,ha,fdr"
        )
        val summaryHeader = listOf(
            "",
            "Summary, easiest average run first. n is the number of fixtures in the window.",
            "team,n,totalFdr,avgFdr"
        )
        text((header + rows + summaryHeader + summary).joinToString("\n"))
    }

    server.addTool(
        name = "get-game-rules",
        description = "The rules a valid FPL squad must satisfy: budget, squad size, how many players " +
            "per position, the maximum from any one club, the legal formations for a starting XI, " +
            "and the transfer sell-on fee. Read this before assembling a squad."
    ) {
        repository.awaitInitialLoad()
        val rules = repository.gameRules.value
            ?: return@addTool text("Game rules are not available - the FPL bootstrap data could not be loaded.")

        val positionLines = rules.positions.map { p ->
            "${p.shortName},${p.name},${p.squadSelect},${p.minPlay},${p.maxPlay}"
        }

        // Enumerated from FPL's own min/max play values rather than hardcoded, so this stays correct
        // if the outfield rules ever change.
        val outfield = rules.positions.filter { it.elementType != 1 }.sortedBy { it.elementType }
        val goalkeepers = rules.positions.firstOrNull { it.elementType == 1 }?.minPlay ?: 1
        val formations = mutableListOf<String>()
        if (outfield.size == 3) {
            val (def, mid, fwd) = outfield
            for (d in def.minPlay..def.maxPlay) for (m in mid.minPlay..mid.maxPlay) for (f in fwd.minPlay..fwd.maxPlay) {
                if (d + m + f == rules.startingXiSize - goalkeepers) formations.add("$d-$m-$f")
            }
        }

        text(
            (listOf(
                "Budget: £${rules.budget.toFixed(1)}m for a squad of ${rules.squadSize}.",
                "Starting XI: ${rules.startingXiSize} players; the other " +
                    "${rules.squadSize - rules.startingXiSize} are substitutes.",
                "Maximum players from any one club: ${rules.clubLimit}.",
                "Selling a player for more than you paid incurs a ${rules.sellOnFeePercent.toFixed(0)}% fee on the profit.",
                "Form is measured over the last ${rules.formDays} days.",
                "",
                "Squad composition. squad = how many of this position in the ${rules.squadSize}-man squad;",
                "minPlay/maxPlay = how many may be in the starting XI.",
                "pos,name,squad,minPlay,maxPlay"
            ) + positionLines + listOf(
                "",
                "Legal formations (DEF-MID-FWD, always exactly $goalkeepers goalkeeper):",
                formations.joinToString(", ").ifEmpty { "unavailable" }
            )).joinToString("\n")
        )
    }

    server.addTool(
        name = "get-gameweek-info",
        description = "Which gameweek is current, which is next, and the deadline to make changes " +
            "before it. Also gives average and highest scores for gameweeks already played."
    ) {
        repository.awaitInitialLoad()
        val gameweeks = repository.gameweeks.value
        if (gameweeks.isEmpty()) {
            return@addTool text("Gameweek data is not available - the FPL bootstrap data could not be loaded.")
        }

        val current = gameweeks.firstOrNull { it.isCurrent }
        val next = gameweeks.firstOrNull { it.id == repository.nextGameweek.value }

        // Only the gameweeks either side of now: the full 38 is mostly unplayed rows with no data.
        val relevant = gameweeks.filter { it.id in (repository.currentGameweek.value - 2)..(repository.nextGameweek.value + 2) }
        val rows = relevant.map { gw ->
            listOf(
                gw.id.toString(),
                csvSafe(gw.name),
                gw.deadlineTime,
                if (gw.finished) "finished" else "upcoming",
                if (gw.finished) gw.averageEntryScore.toString() else "",
                gw.highestScore?.toString() ?: ""
            ).joinToString(",")
        }

        text(
            (listOf(
                "Current gameweek: ${current?.id ?: repository.currentGameweek.value}" +
                    (current?.let { " (${it.name})" } ?: ""),
                "Next gameweek: ${next?.id ?: repository.nextGameweek.value}" +
                    (next?.let { " (${it.name})" } ?: ""),
                "Deadline for the next gameweek: ${next?.deadlineTime ?: "unknown"} (UTC)",
                "",
                "Nearby gameweeks. avg/highest are only set once a gameweek has finished.",
                "gw,name,deadline,state,avg,highest"
            ) + rows).joinToString("\n")
        )
    }

    server.addTool(
        name = "get-fixtures",
        description = "Full fixture list for the season, including results for matches already played"
    ) {
        repository.awaitInitialLoad()
        val fixtures = repository.getFixtures().first()
        text(fixtures.toString())
    }

    server.addTool(
        name = "get-player-history-data",
        description = "Past-season totals for a given player",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("playerId") {
                    put("type", JsonPrimitive("integer"))
                    put("description", JsonPrimitive("The id of the player, as returned by get-players"))
                }
            },
            required = listOf("playerId")
        )
    ) { request ->
        val playerId = intArg(request, "playerId") ?: -1
        val playerHistoryData = repository.getPlayerHistoryData(playerId)
        text(playerHistoryData.toString())
    }

    return server
}

/**
 * Runs an MCP (Model Context Protocol) server using standard I/O for communication.
 *
 * This function initializes a server instance configured with predefined tools and capabilities.
 * It sets up a transport mechanism using standard input and output for communication.
 * Once the server starts, it listens for incoming connections, processes requests,
 * and executes the appropriate tools. The server shuts down gracefully upon receiving
 * a close event.
 */
fun `run mcp server using stdio`() {
    val server = configureServer()
    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered()
    )

    runBlocking {
        server.createSession(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
    }
}
