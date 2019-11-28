package com.example.trello.DI.AppComponent

import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import dagger.Module
import dagger.Provides
import org.scribe.builder.api.TrelloApi
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [TrelloApiModule::class])
class TrelloAuthModule {
    @Provides
    @Singleton
    fun getTrelloAuthService(
        @Named("REST_CONSUMER_KEY") REST_CONSUMER_KEY: String,
        @Named("REST_CONSUMER_SECRET") REST_CONSUMER_SECRET: String,
        @Named("REST_CALLBACK_URL") REST_CALLBACK_URL: String): TrelloAuthService {
        return TrelloAuthService(TrelloApi(), REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL)
    }
}