package com.example.trello.DI.AppComponent

import dagger.Module
import dagger.Provides
import org.scribe.builder.api.TrelloApi
import javax.inject.Named
import javax.inject.Singleton

@Module
class TrelloApiModule {
    // Настройки API
    private val REST_API_INSTANCE = TrelloApi()
    private val REST_URL = "https://api.trello.com/1/"
    private val REST_CONSUMER_KEY = "cc04368b5110c1f0c2d3f9767975d84a"
    private val REST_CONSUMER_SECRET = "3a5c095b6c27c668582b3903e13cb11f072dabd51d5c717b3d4b74ce57608eeb"
    private val REST_CALLBACK_URL = "https://trello_app.com"

    @Provides
    @Singleton
    fun getApiInstace() = REST_API_INSTANCE

    @Provides
    @Singleton
    @Named("REST_URL")
    fun getRestUrl() = REST_URL

    @Provides
    @Singleton
    @Named("REST_CONSUMER_KEY")
    fun getRestConsumerKey() = REST_CONSUMER_KEY

    @Provides
    @Singleton
    @Named("REST_CONSUMER_SECRET")
    fun getRestConsumerSecret() = REST_CONSUMER_SECRET

    @Provides
    @Singleton
    @Named("REST_CALLBACK_URL")
    fun getRestCallbackUrl() = REST_CALLBACK_URL
}