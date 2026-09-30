package dev.johnoreilly.common.ui

import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import kotlinx.serialization.serializer

const val DEEP_LINK_SCHEME = "fplapp"

private val deepLinkMatchers: List<DeepLinkMatcher<*, *>> = listOf(
    // A player link lands on the details pane with the list beneath it: side by side on a wide
    // window, and on a phone back returns to the list rather than out of the app.
    UriDeepLinkMatcher(DeepLinkUri("$DEEP_LINK_SCHEME://player/{playerId}"), serializer<PlayerDetails>())
        .withBackStack { listOf(PlayerList, it.key) },
    UriDeepLinkMatcher(DeepLinkUri("$DEEP_LINK_SCHEME://players"), serializer<PlayerList>()),
    UriDeepLinkMatcher(DeepLinkUri("$DEEP_LINK_SCHEME://fixtures"), serializer<FixtureList>()),
    UriDeepLinkMatcher(DeepLinkUri("$DEEP_LINK_SCHEME://leagues"), serializer<League>()),
    UriDeepLinkMatcher(DeepLinkUri("$DEEP_LINK_SCHEME://assistant"), serializer<Assistant>()),
)

/** The back stack a deep link should open onto, or null if it isn't one of ours. */
internal fun backStackFor(request: DeepLinkRequest): List<Route>? =
    when (val match = deepLinkMatchers.mapNotNull { it.match(request) }.maxOrNull()) {
        null -> null
        is BackStackMatchResult<*, *> -> match.backStack.filterIsInstance<Route>()
        else -> listOfNotNull(match.key as? Route)
    }?.takeIf { it.isNotEmpty() }
