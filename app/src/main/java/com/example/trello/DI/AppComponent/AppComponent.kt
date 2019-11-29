package com.example.trello.DI.AppComponent

import com.example.trello.Activity.LoginActivity
import com.example.trello.DI.ActivityComponent.ActivityComponent
import com.example.trello.DI.ActivityComponent.TrelloServiceModule
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import com.example.trello.Network.TrelloService.TrelloService
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TrelloAuthServiceModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun getTrelloAuthService() : TrelloAuthService

    fun getActivityComponent(trelloService: TrelloServiceModule) : ActivityComponent
}