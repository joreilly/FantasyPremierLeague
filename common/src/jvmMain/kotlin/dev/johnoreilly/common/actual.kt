package dev.johnoreilly.common

import androidx.datastore.core.DataStore
import io.ktor.client.engine.java.*
import org.koin.dsl.module


actual fun platformModule() = module {
    single { Java.create() }
    single { dataStore()}
}

fun dataStore(): DataStore<androidx.datastore.preferences.core.Preferences> =
    createDataStore(
        producePath = { "fpl.preferences_pb" }
    )