package it.niedermann.nextcloud.deck

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.room3.Room
import androidx.room3.RoomDatabase
import it.niedermann.nextcloud.deck.app.shared.Constants.DECK_DB_NAME
import it.niedermann.nextcloud.deck.data.local.DeckDatabase
import kotlinx.coroutines.Dispatchers

class AndroidApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = createAppComponent();
    }

    private fun createAppComponent(): AppComponent {
        val database = getDatabaseBuilder(this).build()
        val dataStore = RxPreferenceDataStoreBuilder(this, DECK_DB_NAME).build()
        val keyValueStore = AndroidKeyValueStore(dataStore)

        return DaggerAppComponent.builder()
            .database(database)
            .keyValueStore(keyValueStore)
            .build()
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