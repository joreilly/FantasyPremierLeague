package dev.johnoreilly.common.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import platform.UIKit.UIViewController

@Suppress("unused") // Used from Swift code
object SharedViewControllers {

    fun playerDetailsViewController(player: Player): UIViewController =
        ComposeUIViewController {
            val viewModel =
                koinViewModel<PlayerDetailsViewModel>(parameters = { parametersOf(player.id) })

            val state by viewModel.state.collectAsState()

            state?.let { state ->
                PlayerDetailsViewShared(state.player, state.history)
            } ?: CircularProgressIndicator()
        }
}