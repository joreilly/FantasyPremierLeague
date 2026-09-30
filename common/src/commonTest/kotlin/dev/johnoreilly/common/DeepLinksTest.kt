package dev.johnoreilly.common

import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import dev.johnoreilly.common.ui.FixtureList
import dev.johnoreilly.common.ui.PlayerDetails
import dev.johnoreilly.common.ui.PlayerList
import dev.johnoreilly.common.ui.backStackFor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeepLinksTest {
    @Test
    fun playerLinkPutsListBeneathDetails() {
        assertEquals(listOf(PlayerList, PlayerDetails(328)), backStackFor(DeepLinkRequest("fplapp://player/328")))
    }

    @Test
    fun tabLinkOpensThatTab() {
        assertEquals(listOf(FixtureList), backStackFor(DeepLinkRequest("fplapp://fixtures")))
    }

    @Test
    fun unknownLinksAreIgnored() {
        assertNull(backStackFor(DeepLinkRequest("fplapp://nope")))
        assertNull(backStackFor(DeepLinkRequest("fplapp://player/not-a-number")))
        assertNull(backStackFor(DeepLinkRequest("https://example.com/player/328")))
    }
}
