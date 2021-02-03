import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.di.initKoin
import org.jetbrains.skija.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository


private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = Window {
    var playerList by remember { mutableStateOf(emptyList<Player>()) }
    var selectedPlayer by remember { mutableStateOf("") }

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

                    LazyColumnFor(items = playerList, itemContent = { player ->
                        PlayerView(player, selectedPlayer) {

                        }
                    })
                }
            }
        )

    }
}


@Composable
fun PlayerView(player: Player, selectedPlayer: String, playerSelected : (player : Player) -> Unit) {
    Row(
        modifier =  Modifier.fillMaxWidth().clickable(onClick = { playerSelected(player) })
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        val imageAsset = fetchImage(player.photoUrl)
        imageAsset?.let {
            Image(it, modifier = Modifier.preferredSize(80.dp))
        }

        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(player.name, style = MaterialTheme.typography.h6)
            Text(player.team, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray)
        }
        Text(player.points.toString())
    }
}




@Composable
fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        loadFullImage(url)?.let {
            image = Image.makeFromEncoded(toByteArray(it)).asImageBitmap()
        }
    }

    return image
}

fun toByteArray(bitmap: BufferedImage) : ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}

fun loadFullImage(source: String): BufferedImage? {
    return try {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: BufferedImage? = ImageIO.read(input)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
