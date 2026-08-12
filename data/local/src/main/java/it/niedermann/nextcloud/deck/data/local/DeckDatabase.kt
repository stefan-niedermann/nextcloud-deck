package it.niedermann.nextcloud.deck.data.local

import androidx.room3.ColumnTypeConverters
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao
import it.niedermann.nextcloud.deck.data.local.dao.CardDao
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithLabelDao
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithPermissionDao
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithUserDao
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao
import it.niedermann.nextcloud.deck.data.local.dao.UserDao
import it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava.CfDaoReturnTypeConverters
import it.niedermann.nextcloud.deck.data.local.daoreturntypeconverter.rxjava.RxDaoReturnTypeConverters
import it.niedermann.nextcloud.deck.data.local.entity.AccountEntity
import it.niedermann.nextcloud.deck.data.local.entity.ActivityEntity
import it.niedermann.nextcloud.deck.data.local.entity.AttachmentEntity
import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity
import it.niedermann.nextcloud.deck.data.local.entity.CardEntity
import it.niedermann.nextcloud.deck.data.local.entity.ColumnEntity
import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity
import it.niedermann.nextcloud.deck.data.local.entity.UserEntity
import it.niedermann.nextcloud.deck.data.local.entity.AccessControlEntity
import it.niedermann.nextcloud.deck.data.local.entity.PermissionEntity
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithLabelEntity
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithPermissionEntity
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithUserEntity
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithLabelEntity
import it.niedermann.nextcloud.deck.data.local.entity.JoinCardWithUserEntity
import it.niedermann.nextcloud.deck.data.local.typeconverter.AttachmentTypeConverter
import it.niedermann.nextcloud.deck.data.local.typeconverter.ColorConverter
import it.niedermann.nextcloud.deck.data.local.typeconverter.OffsetDateTimeConverter
import it.niedermann.nextcloud.deck.data.local.typeconverter.URLConverter
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Database(
    version = 1,
    entities = [
        AccountEntity::class,
        ActivityEntity::class,
        AttachmentEntity::class,
        BoardEntity::class,
        CardEntity::class,
        ColumnEntity::class,
        CommentEntity::class,
        LabelEntity::class,
        UserEntity::class,
        AccessControlEntity::class,
        PermissionEntity::class,
        JoinBoardWithLabelEntity::class,
        JoinBoardWithPermissionEntity::class,
        JoinBoardWithUserEntity::class,
        JoinCardWithLabelEntity::class,
        JoinCardWithUserEntity::class
    ],
    exportSchema = true
)
@DaoReturnTypeConverters(
    value = [
        RxDaoReturnTypeConverters::class,
        CfDaoReturnTypeConverters::class
    ]
)
@ColumnTypeConverters(URLConverter::class, OffsetDateTimeConverter::class, ColorConverter::class, AttachmentTypeConverter::class)
abstract class DeckDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val boardDao: BoardDao
    abstract val columnDao: ColumnDao
    abstract val cardDao: CardDao
    abstract val labelDao: LabelDao
    abstract val attachmentDao: AttachmentDao
    abstract val commentDao: CommentDao
    abstract val accessControlDao: AccessControlDao
    abstract val joinBoardWithLabelDao: JoinBoardWithLabelDao
    abstract val joinBoardWithPermissionDao: JoinBoardWithPermissionDao
    abstract val joinBoardWithUserDao: JoinBoardWithUserDao
    abstract val joinCardWithLabelDao: JoinCardWithLabelDao
    abstract val joinCardWithUserDao: JoinCardWithUserDao
    abstract val userDao: UserDao
    abstract val activityDao: it.niedermann.nextcloud.deck.data.local.dao.ActivityDao

    companion object {

        /**
         * Necessary compatibility layer for Java callers
         */
        fun getDatabaseBuilder(path: Path): Builder<DeckDatabase> {
            return Room.databaseBuilder<DeckDatabase>(name = path.absolutePathString())
                .setDriver(BundledSQLiteDriver())
                .addCallback(object : Callback() {
                    override suspend fun onCreate(connection: SQLiteConnection) {
                        connection.execSQL("INSERT INTO Account (id, url, username, token, accountName) VALUES (-1, 'http://localhost', 'conflict_system', '', 'Conflict System')")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (1)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (2)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (3)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (4)")
                    }
                })
                .fallbackToDestructiveMigration(true)
                .setQueryCoroutineContext(Dispatchers.IO)
        }

        fun getInMemoryDatabaseBuilder(): Builder<DeckDatabase> {
            return Room.inMemoryDatabaseBuilder<DeckDatabase>()
                .setDriver(BundledSQLiteDriver())
                .addCallback(object : Callback() {
                    override suspend fun onCreate(connection: SQLiteConnection) {
                        connection.execSQL("INSERT INTO Account (id, url, username, token, accountName) VALUES (-1, 'http://localhost', 'conflict_system', '', 'Conflict System')")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (1)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (2)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (3)")
                        connection.execSQL("INSERT INTO Permission (id) VALUES (4)")
                    }
                })
                .fallbackToDestructiveMigration(true)
                .setQueryCoroutineContext(Dispatchers.IO)
        }

    }
}
