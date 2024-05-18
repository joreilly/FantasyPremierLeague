package presentation.players

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.model.Player
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

@Composable
fun PlayerView(player: Player,
               selectedPlayer: Player?,
               playerSelected: (player: Player) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = { playerSelected(player) })
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Text(player.name,
                style = if (player.name == selectedPlayer?.name) MaterialTheme.typography.h5 else MaterialTheme.typography.h6)
            Text(player.team, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray)
        }
    }
}

@Composable
fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        loadFullImage(url)?.let {
            image =  org.jetbrains.skia.Image.makeFromEncoded(toByteArray(it)).asImageBitmap()
        }
    }

    return image
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

fun toByteArray(bitmap: BufferedImage): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}