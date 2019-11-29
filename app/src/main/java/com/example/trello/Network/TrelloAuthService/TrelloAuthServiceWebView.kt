package com.example.trello.Network.TrelloAuthService

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Кастомный WebView чтобы авторизоваться в трелло
class TrelloAppWebViewClient(private val REST_CALLBACK_URL: String,
                             private val handler: (uri: Uri) -> Unit ) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val host = Uri.parse(url).host
        if(host != null && host.endsWith("trello.com")) {
            return false
        }

        // Любой путь на наш хост переведем на handler
        val fullpath = Uri.parse(url).scheme + "://" + Uri.parse(url).host
        if(fullpath.endsWith(REST_CALLBACK_URL)) {
            handler(Uri.parse(url))
            // Т.к. уходим с webView удалим историю
            view.loadUrl("about:blank")
            view.clearHistory()
            return false
        }

        // Т.к. уходим с webView удалим историю
        view.loadUrl("about:blank")
        view.clearHistory()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(intent)
        return true
    }
}