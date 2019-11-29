package com.example.trello.DI.AppComponent

import com.example.trello.Activity.LoginActivity
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TrelloAuthModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: LoginActivity)
    fun getTrelloAuthService() : TrelloAuthService
}