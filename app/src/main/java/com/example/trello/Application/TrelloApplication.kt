package com.example.trello.Application

import android.app.Activity
import android.app.Application
import com.example.trello.DI.ActivityComponent.ActivityComponent
import com.example.trello.DI.ActivityComponent.TrelloServiceModule
import com.example.trello.DI.AppComponent.AppComponent
import com.example.trello.DI.AppComponent.DaggerAppComponent

class TrelloApplication: Application() {
    private lateinit var appComponent : AppComponent
    private var activityComponent: ActivityComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().build()
    }

    fun getAppComponent() = appComponent

    fun getActivityComponent() : ActivityComponent {
        if (activityComponent == null) {
            activityComponent = appComponent.getActivityComponent(TrelloServiceModule())
        }

        return activityComponent as ActivityComponent
    }

    fun destroyActivityComponent() {
        activityComponent = null
    }

    companion object {
        fun get(activity: Activity ) = activity.application as TrelloApplication
    }
}