package com.example.trello.DI.ActivityComponent

import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import com.example.trello.Network.TrelloService.TrelloHttpOkInterceptor
import com.example.trello.Network.TrelloService.TrelloService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@Module
class TrelloServiceModule {

    @Provides
    @ActivityScope
    fun getGson() = Gson()

    @Provides
    @ActivityScope
    fun getHttpOkClientWithInterceptor(authService: TrelloAuthService)=
        OkHttpClient().newBuilder()
            .addInterceptor(
                TrelloHttpOkInterceptor(
                    authService
                )
            )
            .build()

    @Provides
    @ActivityScope
    fun getRetrofit(gson: Gson,
                    client: OkHttpClient,
                    @Named("REST_URL") REST_URL: String) = Retrofit.Builder()
        .baseUrl(REST_URL) //This is the onlt mandatory call on Builder object.
        .client(client) //The Htttp client to be used for requests
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @ActivityScope
    fun getTrelloService(retrofit : Retrofit) = retrofit.create(TrelloService::class.java)
}