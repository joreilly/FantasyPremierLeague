package dev.johnoreilly.common.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import org.koin.compose.koinInject
import platform.UIKit.UIViewController

object SharedViewControllers {

    fun playerDetailsViewController(player: Player): UIViewController =
        ComposeUIViewController {
            val viewModel = koinInject<PlayerDetailsViewModel>()

            // TODO cleaner way of managing this?
            var playerHistory by remember { mutableStateOf(emptyList<PlayerPastHistory>()) }
            LaunchedEffect(player) {
                playerHistory = viewModel.getPlayerHistory(player.id)
            }

            PlayerDetailsViewShared(player, playerHistory)
        }
}
