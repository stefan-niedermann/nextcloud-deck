package it.niedermann.nextcloud.deck

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase
import it.niedermann.nextcloud.deck.ui.components.AvatarProvider
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler
import jakarta.inject.Inject

@HiltAndroidApp
class DeckApplication : Application() {

    @Inject
    lateinit var getAvatarUseCase: GetAvatarUseCase

    override fun onCreate() {
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }
        ExceptionHandler.initialize(this)
        super.onCreate()
        AvatarProvider.initialize(getAvatarUseCase)
    }
}
