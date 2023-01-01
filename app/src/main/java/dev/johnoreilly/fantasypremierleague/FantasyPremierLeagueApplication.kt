package dev.johnoreilly.fantasypremierleague

import android.app.Application
import co.touchlab.kermit.Logger
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.fantasypremierleague.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class FantasyPremierLeagueApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@FantasyPremierLeagueApplication)
            modules(appModule)
        }

        Logger.d { "FantasyPremierLeagueApplication" }
    }
}