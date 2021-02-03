import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.domain.entities.Player
import presentation.players.PlayerView


private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = Window {
    var playerList by remember { mutableStateOf(emptyList<Player>()) }
    val selectedPlayer by remember { mutableStateOf("") }
    val repository = koin.get<FantasyPremierLeagueRepository>()

    LaunchedEffect(true) {
        playerList = repository.getPlayers()
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Fantasy Premier League") })
            },
            bodyContent = {
                Row(Modifier.fillMaxSize()) {
                    LazyColumn {
                        items(items = playerList, itemContent = { player ->
                            PlayerView(player, selectedPlayer) {
                            }
                        })
                    }
                }
            }
        )

    }
}