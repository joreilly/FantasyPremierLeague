package dev.johnoreilly.common

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.engine.darwin.*
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults


@OptIn(ExperimentalSettingsApi::class)
actual fun platformModule() = module {
    single { Darwin.create() }
    single<ObservableSettings> { AppleSettings(NSUserDefaults.standardUserDefaults) }
}



