import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import kotlinx.coroutines.flow.*
import presentation.players.PlayerView


private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = application {
    val windowState = rememberWindowState()

    val selectedPlayer by remember { mutableStateOf("") }
    val repository = koin.get<FantasyPremierLeagueRepository>()

    val searchQuery = MutableStateFlow("")
    val playerList by searchQuery.debounce(250).flatMapLatest { searchQuery ->
        repository.playerList.mapLatest { playerList ->
            playerList
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .sortedByDescending { it.points }
        }
    }.collectAsState(emptyList())


    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Fantasy Premier League"
    ) {
        Row(Modifier.fillMaxSize()) {
            LazyColumn {
                items(items = playerList, itemContent = { player ->
                    PlayerView(player, selectedPlayer) {
                    }
                })
            }
        }
    }
}