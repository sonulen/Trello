package com.example.trello.DI.ActivityComponent

import com.example.trello.Network.TrelloClient.TrelloClient
import dagger.Subcomponent

@Subcomponent(modules = [TrelloClientModule::class])
@ActivityScope
interface ActivityComponent {
    fun getTrelloClient() : TrelloClient
}