package com.example.trello.Application

import android.app.Activity
import android.app.Application
import com.example.trello.DI.AppComponent.AppComponent
import com.example.trello.DI.AppComponent.DaggerAppComponent

class TrelloApplication: Application() {
    private lateinit var appComponent : AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().build()
    }

    fun getAppComponent() = appComponent

    companion object {
        fun get(activity: Activity ) = activity.application as TrelloApplication
    }
}