import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonObject


private val koin = initKoin(enableNetworkLogs = true).koin

fun configureServer(): Server {
    val fantasyPremierLeagueRepository = koin.get<FantasyPremierLeagueRepository>()

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
        description = "List of players"
    ) {
        val players = fantasyPremierLeagueRepository.getPlayers().first()
        CallToolResult(
            content = listOf(TextContent(players.toString()))
        )
    }

    server.addTool(
        name = "get-fixtures",
        description = "List of fixtures"
    ) {
        val fixtures = fantasyPremierLeagueRepository.getFixtures().first()
        CallToolResult(
            content = listOf(TextContent(fixtures.toString()))
        )
    }

    server.addTool(
        name = "get-player-history-data",
        description = "List of fixtures",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                putJsonObject("playerId") { put("type", JsonPrimitive("int")) }
            },
            required = listOf("playerId")
        )
    ) { request ->
        val playerId = request.arguments["playerId"]?.jsonPrimitive?.int ?: -1
        val playerHistoryData = fantasyPremierLeagueRepository.getPlayerHistoryData(playerId)
        CallToolResult(
            content = listOf(TextContent(playerHistoryData.toString()))
        )
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
        server.connect(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
    }
}

/**
 * Launches an SSE (Server-Sent Events) MCP (Model Context Protocol) server on the specified port.
 * This server enables clients to connect via SSE for real-time communication and provides endpoints
 * for handling specific messages.
 *
 * @param port The port number on which the SSE server should be started.
 */
fun `run sse mcp server`(port: Int): Unit = runBlocking {
    val server = configureServer()
    embeddedServer(CIO, host = "0.0.0.0", port = port) {
        mcp {
            server
        }
    }.start(wait = true)
}
