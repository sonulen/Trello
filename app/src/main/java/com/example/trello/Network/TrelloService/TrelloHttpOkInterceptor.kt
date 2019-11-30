package com.example.trello.Network.TrelloService

import android.util.Log
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import okhttp3.Interceptor
import okhttp3.Response

class TrelloHttpOkInterceptor(val authService: TrelloAuthService) : Interceptor {
    // Добавим к каждому запросу к Trello ключ приложения и verifer
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("key", authService.CONSUMER_KEY)
            .addQueryParameter("token", authService.ACCESS_TOKEN)
            .build()

        val request = chain.request().newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}