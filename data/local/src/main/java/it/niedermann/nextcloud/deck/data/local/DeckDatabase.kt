package it.niedermann.nextcloud.deck.data.local

import androidx.room3.ColumnTypeConverters
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao
import it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava.CfDaoReturnTypeConverters
import it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava.RxDaoReturnTypeConverters
import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity
import it.niedermann.nextcloud.deck.data.local.typeconverter.URLConverter
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Database(
    version = 1,
    entities = [AccountEntity::class],
    exportSchema = true
)
@DaoReturnTypeConverters(
    value = [
        RxDaoReturnTypeConverters::class,
        CfDaoReturnTypeConverters::class
    ]
)
@ColumnTypeConverters(URLConverter::class)
abstract class DeckDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao

    companion object {

        /**
         * Necessary compatibility layer for Java callers
         */
        fun getDatabaseBuilder(path: Path): Builder<DeckDatabase> {
            return Room.databaseBuilder<DeckDatabase>(name = path.absolutePathString())
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(true)
                .setQueryCoroutineContext(Dispatchers.IO)
        }

        fun getInMemoryDatabaseBuilder(): Builder<DeckDatabase> {
            return Room.inMemoryDatabaseBuilder<DeckDatabase>()
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(true)
                .setQueryCoroutineContext(Dispatchers.IO)
        }

    }
}
