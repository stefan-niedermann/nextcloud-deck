package it.niedermann.nextcloud.deck

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase
import it.niedermann.nextcloud.deck.ui.components.AvatarProvider
import javax.inject.Inject

@HiltAndroidApp
class DeckApplication : Application() {

    @Inject
    lateinit var getAvatarUseCase: GetAvatarUseCase

    override fun onCreate() {
        super.onCreate()
        AvatarProvider.initialize(getAvatarUseCase)
    }
}
