package com.example.trello.Network.TrelloAuthService

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

// Кастомный WebView чтобы авторизоваться в трелло
class MyAppWebViewClient(REST_CALLBACK_URL: String) : WebViewClient() {
    private val REST_CALLBACK_URL: String = REST_CALLBACK_URL

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val host = Uri.parse(url).host
        if(host != null && host.endsWith("trello.com")) {
            return false
        }

        // Любой путь на наш хост переведем на LoginActivity
        val fullpath = Uri.parse(url).scheme + "://" + Uri.parse(url).host
        if(fullpath.endsWith(REST_CALLBACK_URL)) {
            view.goBack()
            val intent = Intent(view.context, LoginActivity::class.java)
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            view.context.startActivity(intent)
            return false
        }

        // view.goBack() сделал из-за того что когда мы делаем back
        // с браузера попадать на корректную страницу (иначе там белый экран)
        view.goBack()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view.context.startActivity(intent)
        return true
    }
}