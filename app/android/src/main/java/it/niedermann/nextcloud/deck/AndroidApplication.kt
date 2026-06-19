package it.niedermann.nextcloud.deck

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room3.Room
import androidx.room3.RoomDatabase
import it.niedermann.nextcloud.deck.app.shared.Constants.DECK_DB_NAME
import it.niedermann.nextcloud.deck.data.local.DeckDatabase
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath

class AndroidApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = createAppComponent();
    }

    private fun createAppComponent(): AppComponent {
        val database = getDatabaseBuilder(this).build()
        val storePath = filesDir.resolve(DECK_DB_NAME).absolutePath.toPath()
        val store = PreferenceDataStoreFactory.createWithPath(produceFile = { storePath })
        val keyValueStore = AndroidKeyValueStore(store)

        return DaggerAppComponent.create(database, keyValueStore)
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }

    fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DeckDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DECK_DB_NAME)
        return Room.databaseBuilder<DeckDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        ).setQueryCoroutineContext(Dispatchers.IO)
    }
}