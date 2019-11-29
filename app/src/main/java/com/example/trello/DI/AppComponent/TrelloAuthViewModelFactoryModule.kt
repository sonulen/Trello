package com.example.trello.DI.AppComponent

import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import com.example.trello.ViewModels.TrelloAuthService.TrelloAuthServiceViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [TrelloAuthServiceModule::class])
class TrelloAuthServiceViewModelFactoryModule {
    @Provides
    @Singleton
    fun getTrelloAuthServiceViewModelFactory(authService: TrelloAuthService)
            = TrelloAuthServiceViewModelFactory(authService)
}
