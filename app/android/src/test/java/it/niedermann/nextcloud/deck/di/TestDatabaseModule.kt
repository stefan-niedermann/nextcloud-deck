package it.niedermann.nextcloud.deck.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import it.niedermann.nextcloud.deck.AndroidKeyValueStore
import it.niedermann.nextcloud.deck.app.shared.Constants.DECK_DB_NAME
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule
import it.niedermann.nextcloud.deck.data.local.DeckDatabase
import it.niedermann.nextcloud.deck.data.local.KeyValueStore
import it.niedermann.nextcloud.remote.ApiProvider
import org.mockito.Mockito.mock
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeckDatabase {
        return Room.inMemoryDatabaseBuilder(context, DeckDatabase::class.java)
            .setDriver(BundledSQLiteDriver())
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): RxDataStore<Preferences> {
        return RxPreferenceDataStoreBuilder(context, DECK_DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideKeyValueStore(dataStore: RxDataStore<Preferences>): KeyValueStore {
        return AndroidKeyValueStore(dataStore)
    }
}

@Module(includes = [SharedModule::class])
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {
    @Provides
    @Singleton
    fun provideApiProviderFactory(): ApiProvider.Factory {
        return mock(ApiProvider.Factory::class.java)
    }
}
