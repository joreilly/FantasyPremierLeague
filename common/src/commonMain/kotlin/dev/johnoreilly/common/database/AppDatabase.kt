package dev.johnoreilly.common.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.Team
import kotlinx.datetime.LocalDateTime

internal expect object AppDatabaseCtor : RoomDatabaseConstructor<AppDatabase>

@Database(entities = [Team::class, Player::class, GameFixture::class], version = 1)
@ConstructedBy(AppDatabaseCtor::class)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fantasyPremierLeagueDao(): FantasyPremierLeagueDao
}

internal const val dbFileName = "fantasypremierleague.db"


class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}
