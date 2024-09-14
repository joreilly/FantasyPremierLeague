package dev.johnoreilly.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory

@Preview
@Composable
fun PlayerDetailsViewSharedPreview() {
    val player = Player(
        1, "Mo Salah", "Liverpool",
        "", 98, 10.0, 14, 1
    )
    val playerPastHistory1 = PlayerPastHistory("2021", 50)
    val playerPastHistory2 = PlayerPastHistory("2022", 75)
    val playerPastHistory3 = PlayerPastHistory("2023", 60)

    val playerHistory = listOf(playerPastHistory1, playerPastHistory2, playerPastHistory3)
    PlayerDetailsViewShared(player, playerHistory)
}