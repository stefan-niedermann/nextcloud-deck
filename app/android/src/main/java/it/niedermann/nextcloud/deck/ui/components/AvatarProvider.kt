package it.niedermann.nextcloud.deck.ui.components

import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase

object AvatarProvider {
    private var getAvatarUseCase: GetAvatarUseCase? = null

    fun initialize(useCase: GetAvatarUseCase) {
        if (getAvatarUseCase != null) {
            throw IllegalStateException("AvatarProvider already initialized")
        }
        getAvatarUseCase = useCase
    }

    fun get(): GetAvatarUseCase {
        return getAvatarUseCase ?: throw IllegalStateException("AvatarProvider not yet initialized")
    }
}
