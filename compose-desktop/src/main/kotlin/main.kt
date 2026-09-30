import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.johnoreilly.common.di.initKoin
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import dev.johnoreilly.common.ui.App


private val koin = initKoin(enableNetworkLogs = true).koin

// A deep link can be passed as the first argument, e.g. ./gradlew run --args="fplapp://player/328"
fun main(args: Array<String>) {
    val deepLink = args.firstOrNull()?.let { DeepLinkRequest(it) }

    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Fantasy Premier League"
        ) {
            App(deepLink)
        }
    }
}
