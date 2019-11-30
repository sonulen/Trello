package com.example.trello.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.trello.Application.TrelloApplication
import com.example.trello.Network.TrelloAuthService.AuthState
import com.example.trello.R
import com.example.trello.ViewModels.TrelloAuthService.TrelloAuthServiceViewModel
import com.example.trello.ViewModels.TrelloAuthService.TrelloAuthServiceViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

val EXTRAS_NAME_TO_REMOVE_ACCESS_TOKEN = "REMOVE_ACCESS_TOKEN"

class LoginActivity : AppCompatActivity() {

    private val APP_NAME = "Trello"
    private val KEY_NAME_ACCESS_TOKEN = "ACCESS_TOKEN"

    @Inject
    lateinit var trelloAuthServiceViewModelFactory : TrelloAuthServiceViewModelFactory

    lateinit var trelloAuthServiceViewModel: TrelloAuthServiceViewModel

    override fun onStart() {
        super.onStart()
        showUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Вводим зависимости в нашем случае это VM для TrelloAuthService
        TrelloApplication.get(this).getAppComponent().inject(this)

        trelloAuthServiceViewModel = ViewModelProviders
            .of(this,trelloAuthServiceViewModelFactory)[TrelloAuthServiceViewModel::class.java]

        // Это сделанно для сценария
        // Когда с HomeActivity нажмут LogOut сюда должен прилететь Intent
        // С параметром EXTRAS_NAME_TO_REMOVE_ACCESS_TOKEN и значением true
        intent.extras?.let{
            if (it.getString(EXTRAS_NAME_TO_REMOVE_ACCESS_TOKEN) == "true") {
                removeTokenFromSharedPreference()
            }
        }

        trelloAuthServiceViewModel.getState().observe(this, Observer {
            when(it) {
                AuthState.NONE -> {
                    showLoadingImage()
                    checkSharedPreference()
                    showUI()
                }
                AuthState.CONNECTING -> hideUI()
                AuthState.CONNECTED -> {
                    saveToken(trelloAuthServiceViewModel.getAccessToken())
                    processSuccessConnected()
                }
                AuthState.FAILED -> processFailedConnected()
                else -> showUI()
            }
        })

        btn_auth_trello.setOnClickListener {
            hideUI()
            trelloAuthServiceViewModel.connect(trello_webview)
        }

        // Это сценарий в случае если мы приходим сюда как Intent View из браузера
        if (intent.data is Uri) {
            val uri = intent.data as Uri
            trelloAuthServiceViewModel.processVerifer(uri)
        }
    }

    private fun saveToken(accessToken: String) {
        val preferences: SharedPreferences =
            this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(KEY_NAME_ACCESS_TOKEN, accessToken).apply()
    }

    private fun checkSharedPreference() {
        val preferences: SharedPreferences =
            this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val retrivedToken = preferences.getString(KEY_NAME_ACCESS_TOKEN, null)
        retrivedToken?.let {
            trelloAuthServiceViewModel.updateToken(it)
        }
    }

    private fun removeTokenFromSharedPreference() {
        val preferences: SharedPreferences =
            this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        trelloAuthServiceViewModel.reset()
        preferences.edit().remove(KEY_NAME_ACCESS_TOKEN).commit()
    }

    fun showLoadingImage() {
        loading_image.visibility = View.VISIBLE
        ui_layout.visibility = View.INVISIBLE
        iv_trello_logo.visibility = View.INVISIBLE
        trello_webview.visibility = View.INVISIBLE

    }

    fun showUI() {
        loading_image.visibility = View.INVISIBLE
        ui_layout.visibility = View.VISIBLE
        iv_trello_logo.visibility = View.VISIBLE
        trello_webview.visibility = View.INVISIBLE
    }

    fun hideUI() {
        loading_image.visibility = View.INVISIBLE
        ui_layout.visibility = View.INVISIBLE
        iv_trello_logo.visibility = View.INVISIBLE
        trello_webview.visibility = View.VISIBLE
    }

    fun processSuccessConnected() {
        showLoadingImage()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    fun processFailedConnected() {
        showUI()
        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
    }
}
