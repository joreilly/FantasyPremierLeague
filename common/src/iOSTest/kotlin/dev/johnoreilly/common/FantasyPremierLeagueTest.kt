package dev.johnoreilly.common

import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.data.remote.FantasyPremierLeagueApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue


class FantasyPremierLeagueTest {
    @Test
    fun testGetFixtures() {
        GlobalScope.launch(Dispatchers.Main) {
            val koin = initKoin(enableNetworkLogs = true).koin
            val api = koin.get<FantasyPremierLeagueApi>()
            val result = api.fetchFixtures()
            println(result)
            assertTrue(result.isNotEmpty())
        }
    }
}
