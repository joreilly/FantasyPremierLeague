package dev.johnoreilly.common.ui

import androidx.compose.ui.window.ComposeUIViewController
import dev.johnoreilly.common.domain.entities.Player
import platform.UIKit.UIViewController

object SharedViewControllers {

    fun playerDetailsViewController(player: Player): UIViewController =
        ComposeUIViewController {
            PlayerDetailsViewShared(player)
        }
}
