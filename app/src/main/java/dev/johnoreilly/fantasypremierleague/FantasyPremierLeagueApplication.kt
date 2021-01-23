package dev.johnoreilly.fantasypremierleague

import android.app.Application
import co.touchlab.kermit.Kermit
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.fantasypremierleague.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.inject

class FantasyPremierLeagueApplication : Application(), KoinComponent {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@FantasyPremierLeagueApplication)
            modules(appModule)
        }

        logger.d { "FantasyPremierLeagueApplication" }
    }
}