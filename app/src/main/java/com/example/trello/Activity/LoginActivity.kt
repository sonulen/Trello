package com.example.trello.Activity

import android.content.Intent
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


class LoginActivity : AppCompatActivity() {

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

        trelloAuthServiceViewModel.getState().observe(this, Observer {
            when(it) {
                AuthState.NONE -> showUI()
                AuthState.CONNECTING -> hideUI()
                AuthState.CONNECTED -> processSuccessConnected()
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

    fun showUI() {
        ui_layout.visibility = View.VISIBLE
        iv_trello_logo.visibility = View.VISIBLE
        trello_webview.visibility = View.INVISIBLE
    }

    fun hideUI() {
        ui_layout.visibility = View.INVISIBLE
        iv_trello_logo.visibility = View.INVISIBLE
        trello_webview.visibility = View.VISIBLE
    }

    fun processSuccessConnected() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    fun processFailedConnected() {
        showUI()
        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
    }
}
