import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import dev.johnoreilly.common.ui.PlayerDetailsViewShared
import dev.johnoreilly.common.ui.PlayerDetailsViewSharedPreview

@PreviewTest
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    //MaterialTheme {
        //PlayerDetailsViewSharedPreview()
    //}

    val player = Player(
        1, "Mo Salah", "Liverpool",
        "", 98, 20.0, 14, 1
    )
    val playerPastHistory1 = PlayerPastHistory("2021", 50)
    val playerPastHistory2 = PlayerPastHistory("2022", 75)
    val playerPastHistory3 = PlayerPastHistory("2023", 60)

    val playerHistory = listOf(playerPastHistory1, playerPastHistory2, playerPastHistory3)
    PlayerDetailsViewShared(player, playerHistory)
}

