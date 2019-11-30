package com.example.trello.ViewModels.TrelloAuthService

import android.net.Uri
import android.webkit.WebView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trello.Network.TrelloAuthService.AuthState
import com.example.trello.Network.TrelloAuthService.TrelloAuthService
import javax.inject.Inject



class TrelloAuthServiceViewModel
    constructor(authService: TrelloAuthService) : ViewModel() {
    private val trelloAuthService: TrelloAuthService = authService

    fun connect(webView: WebView) {
        trelloAuthService.connect(webView)
    }

    fun getState() : LiveData<AuthState> = trelloAuthService.getState()
    fun getAccessToken() = trelloAuthService.ACCESS_TOKEN
    fun processVerifer(uri: Uri) = trelloAuthService.parseUri(uri)
    fun updateToken(token: String) = trelloAuthService.updateAccessToken(token)
}