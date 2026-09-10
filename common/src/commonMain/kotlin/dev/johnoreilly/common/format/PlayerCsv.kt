package dev.johnoreilly.common.format

import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import kotlinx.coroutines.flow.first
import kotlin.math.abs
import kotlin.math.floor

/**
 * Renders the player pool as compact CSV for LLM consumption, shared by the Koog agent's
 * getPlayers tool and the MCP server's get-players.
 *
 * Row-oriented CSV rather than JSON or a data-class toString: with ~650 players, repeating a field
 * name per value costs more than the values themselves, and the whole pool has to fit in a context
 * window alongside the model's actual reasoning.
 */

const val PLAYER_CSV_HEADER: String =
    "id,name,pos,team,price,pts,form,ppg,mins,sel,ep,xgi90,bonus,cs,gc,saves,pen,sp,status,news"

private val DEFAULT_POSITION_NAMES = mapOf(1 to "GKP", 2 to "DEF", 3 to "MID", 4 to "FWD")

/**
 * Fixed-decimal formatting built from integer arithmetic. Deliberately not `String.format`, which
 * is JVM-only and, worse, locale-sensitive - a comma-decimal default locale would emit "6,0" and
 * silently corrupt every row of the CSV.
 */
fun Double.toFixed(decimals: Int): String {
    if (isNaN() || isInfinite()) return "0"
    var factor = 1L
    repeat(decimals) { factor *= 10 }
    // floor(x + 0.5) gives half-up rounding. kotlin.math.round would round ties to even, so 0.25
    // at one decimal would render as "0.2" - not what a reader of a price or form figure expects.
    val scaled = floor(abs(this) * factor + 0.5).toLong()
    val whole = scaled / factor
    val fraction = scaled % factor
    val sign = if (this < 0 && scaled != 0L) "-" else ""
    return if (decimals <= 0) "$sign$whole"
    else "$sign$whole." + fraction.toString().padStart(decimals, '0')
}

/** Commas and newlines would break the row apart; news text in particular contains both. */
fun csvSafe(value: String): String = value.replace(',', ';').replace('\n', ' ').trim()

/** Position short names as FPL reports them, falling back if the bootstrap load failed. */
fun FantasyPremierLeagueRepository.positionShortNames(): Map<Int, String> =
    gameRules.value?.positions?.associate { it.elementType to it.shortName }
        ?: DEFAULT_POSITION_NAMES

/**
 * Team abbreviations keyed by the index fixtures reference them by, which is what both the fixture
 * list and [Player.teamId] were matched on when the repository built them - not the primary key.
 */
suspend fun FantasyPremierLeagueRepository.teamShortNames(): Map<Int, String> =
    getTeams().first().associate { it.index to it.shortName.ifEmpty { it.name } }

fun playerCsvLegend(playerCount: Int, formDays: Int): List<String> = listOf(
    "$playerCount players, best first. price is in £m. form = points per match over the " +
        "last $formDays days. sel = percent of managers who own him.",
    "ep = FPL's expected points for the next gameweek. xgi90 = expected goal involvements per 90 minutes.",
    "cs = clean sheets, gc = goals conceded, saves is goalkeepers only.",
    "pen/sp = set-piece order, 1 = first choice, blank = not on duty; sp covers free kicks and corners.",
    "status: a=available, d=doubtful, i=injured, s=suspended, u=unavailable, n=not in squad.",
    "Blank news means nothing to report.",
    "",
    PLAYER_CSV_HEADER
)

fun playerCsvRow(player: Player, positionName: String, teamName: String): String = listOf(
    player.id.toString(),
    csvSafe(player.webName.ifEmpty { player.name }),
    positionName,
    teamName,
    player.currentPrice.toFixed(1),
    player.points.toString(),
    player.form.toFixed(1),
    player.pointsPerGame.toFixed(1),
    player.minutes.toString(),
    player.selectedByPercent.toFixed(1),
    player.expectedPointsNext?.toFixed(1) ?: "",
    player.expectedGoalInvolvementsPer90.toFixed(2),
    player.bonus.toString(),
    player.cleanSheets.toString(),
    player.goalsConceded.toString(),
    player.saves.toString(),
    player.penaltiesOrder?.toString() ?: "",
    listOfNotNull(player.directFreekicksOrder, player.cornersOrder).minOrNull()?.toString() ?: "",
    player.status,
    csvSafe(player.news)
).joinToString(",")

/**
 * The whole player pool as a legend plus one CSV row per player, best first.
 *
 * @param minMinutes drops players below this many minutes played; 1 excludes everyone who has
 *   never featured, 0 (the default) keeps the full pool so nothing is hidden from the model.
 */
suspend fun FantasyPremierLeagueRepository.playersAsCsv(minMinutes: Int = 0): String {
    awaitInitialLoad()

    val positions = positionShortNames()
    val teams = teamShortNames()
    val players = getPlayers().first()
        .filter { it.minutes >= minMinutes }
        .sortedByDescending { it.points }

    val rows = players.map { player ->
        playerCsvRow(
            player = player,
            positionName = positions[player.elementType] ?: player.elementType.toString(),
            teamName = teams[player.teamId] ?: csvSafe(player.team)
        )
    }

    return (playerCsvLegend(players.size, gameRules.value?.formDays ?: 30) + rows).joinToString("\n")
}
