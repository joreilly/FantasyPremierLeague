package dev.johnoreilly.common

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.engine.java.*
import org.koin.dsl.module
import java.util.prefs.Preferences


actual fun platformModule() = module {
    single { Java.create() }
    single<ObservableSettings> { PreferencesSettings(Preferences.userRoot()) }
}

