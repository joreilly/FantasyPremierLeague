package dev.johnoreilly.common.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.ComposeUIViewController
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import kotlinx.coroutines.flow.MutableStateFlow
import platform.UIKit.UIViewController

object SharedViewControllers {

    private val playerHistoryState = MutableStateFlow<List<PlayerPastHistory>>(emptyList())

    fun playerDetailsViewController(player: Player): UIViewController =
        ComposeUIViewController {
            val playerHistoryComposeState = playerHistoryState.collectAsState(emptyList())
            PlayerDetailsViewShared(player, playerHistoryComposeState.value)
        }

    fun updatePlayerHistory(playerHistory: List<PlayerPastHistory>) {
        playerHistoryState.value = playerHistory
    }
}
