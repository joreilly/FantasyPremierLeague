package dev.johnoreilly.common

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.engine.android.*
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
actual fun platformModule() = module {
    single { Android.create() }
    single<ObservableSettings> { createObservableSettings(get()) }
}


@OptIn(ExperimentalSettingsApi::class)
private fun createObservableSettings(context: Context): ObservableSettings {
    return AndroidSettings(context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE))
}
