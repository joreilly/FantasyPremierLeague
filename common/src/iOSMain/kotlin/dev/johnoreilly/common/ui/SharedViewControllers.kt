package dev.johnoreilly.common.ui

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("unused") // Used from Swift code
object SharedViewControllers {
    private val deepLink = MutableStateFlow<DeepLinkRequest?>(null)

    /** Called from SwiftUI's onOpenURL, for both cold launches and links while running. */
    fun handleDeepLink(url: String) {
        deepLink.value = DeepLinkRequest(url)
    }

    fun mainViewController() =
        ComposeUIViewController {
            val link by deepLink.collectAsState()
            App(link, onDeepLinkHandled = { deepLink.value = null })
        }
}
