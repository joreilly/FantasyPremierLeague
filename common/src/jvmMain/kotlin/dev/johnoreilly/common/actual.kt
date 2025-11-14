package dev.johnoreilly.common

import androidx.datastore.core.DataStore
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.johnoreilly.common.database.AppDatabase
import dev.johnoreilly.common.database.dbFileName
import io.ktor.client.engine.java.*
import org.koin.dsl.module
import java.io.File


actual fun platformModule() = module {
    single { Java.create() }
    single { dataStore()}
    single<AppDatabase> { createRoomDatabase() }
}


fun createRoomDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath,)
        .setDriver(BundledSQLiteDriver())
        .build()
}

fun dataStore(): DataStore<androidx.datastore.preferences.core.Preferences> =
    createDataStore(
        producePath = { "fpl.preferences_pb" }
    )