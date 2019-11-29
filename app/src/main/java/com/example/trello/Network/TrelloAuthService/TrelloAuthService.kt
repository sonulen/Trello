package com.example.trello.Network.TrelloAuthService

import android.net.Uri
import android.webkit.WebView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.TrelloApi
import org.scribe.oauth.OAuthService

enum class AuthState {
    NONE,
    CONNECTING,
    CONNECTED,
    FAILED
}

class TrelloAuthService(
    REST_API_INSTANCE: TrelloApi,
    REST_CONSUMER_KEY: String,
    REST_CONSUMER_SECRET: String,
    REST_CALLBACK_URL: String
) {
    private var state = MutableLiveData<AuthState>(AuthState.NONE)

    private lateinit var service : OAuthService ///< Сервис для oAuth1
    private val CALLBACK_URL: String = REST_CALLBACK_URL

    private lateinit var authorizationUrl : String

    @Volatile private var serviceInited: Boolean = false

    // Данные которые нужны для последующих запросов

    val CONSUMER_KEY: String = REST_CONSUMER_KEY ///< Ключ приложения
    var oAuthVerifer: String = "" ///< Verifer полученный от Trello
        get() = field
        private set(value) {
            field = value
        }

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

            serviceInited = true
        }
    }

    fun getState() : LiveData<AuthState> = state

    fun connect(wbV: WebView) {
        // Отобразим встроенный браузер
        wbV.settings.javaScriptEnabled = true
        var client = TrelloAppWebViewClient(CALLBACK_URL, this::handlerForAnswerUri)
        wbV.webViewClient = client

        if (serviceInited) {
            wbV.loadUrl(authorizationUrl)
            state.value = AuthState.CONNECTING
        } else {
            state.value = AuthState.FAILED
        }
    }

    private fun handlerForAnswerUri(uri: Uri) {
        parseUri(uri)
    }

    fun parseUri(uri: Uri) {
        val oauth_token = uri.getQueryParameter("oauth_token")
        val oauth_verifier = uri.getQueryParameter("oauth_verifier")
        if (oauth_token != null && oauth_verifier != null) {
            saveVerifer(oauth_verifier)
        } else {
            state.value = AuthState.FAILED
        }
    }

    private fun saveVerifer(oauthVerifier: String) {
        oAuthVerifer = oauthVerifier
        state.value = AuthState.CONNECTED
    }


}