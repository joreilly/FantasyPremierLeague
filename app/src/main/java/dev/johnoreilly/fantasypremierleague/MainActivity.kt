package dev.johnoreilly.fantasypremierleague

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import dev.johnoreilly.common.ui.App


class MainActivity : ComponentActivity() {

    private var deepLink by mutableStateOf<DeepLinkRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // On recreation the restored back stack wins; re-applying the launch intent would undo
        // any navigation since.
        if (savedInstanceState == null) deepLink = intent.toDeepLinkRequest()

        setContent {
            App(deepLink, onDeepLinkHandled = { deepLink = null })
        }
    }

    // singleTop: links arriving while we're running come here rather than starting a new activity.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        deepLink = intent.toDeepLinkRequest()
    }

    private fun Intent.toDeepLinkRequest() = if (data != null) DeepLinkRequest(this) else null
}
