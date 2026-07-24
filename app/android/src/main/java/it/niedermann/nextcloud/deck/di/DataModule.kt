package it.niedermann.nextcloud.deck.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule

@Module(includes = [SharedModule::class])
@InstallIn(SingletonComponent::class)
object DataModule
