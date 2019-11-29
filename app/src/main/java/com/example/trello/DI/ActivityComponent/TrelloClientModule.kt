package com.example.trello.DI.ActivityComponent

import com.example.trello.Network.TrelloClient.TrelloClient
import com.example.trello.Network.TrelloService.TrelloService
import dagger.Module
import dagger.Provides

@Module(includes = [TrelloServiceModule::class])
class TrelloClientModule {
    @Provides
    @ActivityScope
    fun getTrelloClientModule(authService: TrelloService) = TrelloClient(authService)
}
