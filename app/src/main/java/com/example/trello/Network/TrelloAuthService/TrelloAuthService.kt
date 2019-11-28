package com.example.trello.Network.TrelloAuthService

import android.webkit.WebView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.TrelloApi
import org.scribe.oauth.OAuthService

class TrelloAuthService(
    REST_API_INSTANCE: TrelloApi,
    REST_CONSUMER_KEY: String,
    REST_CONSUMER_SECRET: String,
    REST_CALLBACK_URL: String
) {
    // Сервис для oAuth1
    private lateinit var service : OAuthService
    private val CALLBACK_URL: String = REST_CALLBACK_URL
    private lateinit var authorizationUrl : String

    @Volatile private var serviceInited: Boolean = false

    init {
        GlobalScope.launch {
            service = ServiceBuilder()
                .provider(REST_API_INSTANCE)
                .apiKey(REST_CONSUMER_KEY)
                .apiSecret(REST_CONSUMER_SECRET)
                .scope("read,write")
                .callback(REST_CALLBACK_URL)
                .build()

            var requestToken = service.requestToken

            authorizationUrl = service.getAuthorizationUrl(requestToken) + "&name=Trello&scope=read,write"

            // Получили Url все окей, можно работать
            serviceInited = true
        }
    }

    fun connect(wbV: WebView? = null): Boolean {

        if (!serviceInited) {
            return false
        }

        // В VM надо будет сделать STATE типо CONNECTING FAILED SUCCESS

        if (wbV != null) {
            // Отобразим встроенный браузер
            wbV.settings.javaScriptEnabled = true
            wbV.webViewClient = MyAppWebViewClient(CALLBACK_URL)
            wbV.loadUrl(authorizationUrl)
        } else {
            // Бросим Intent на открытие браузера
            // TODO
        }

        // Не должны мы доходить до сюда
        // Через Intent должны открыть другую Activity
        return false
    }
}