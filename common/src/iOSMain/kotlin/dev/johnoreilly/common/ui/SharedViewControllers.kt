package dev.johnoreilly.common.ui

import androidx.compose.ui.window.ComposeUIViewController

@Suppress("unused") // Used from Swift code
object SharedViewControllers {
    fun mainViewController() =
        ComposeUIViewController {
            App()
        }
}