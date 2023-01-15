package dev.johnoreilly.common

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.engine.darwin.*
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults


actual fun platformModule() = module {
    single { Darwin.create() }
    single<ObservableSettings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}



