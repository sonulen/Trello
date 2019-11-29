package com.example.trello.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.trello.Application.TrelloApplication
import com.example.trello.R
import com.example.trello.ViewModels.TrelloAuthService.TrelloAuthServiceViewModel
import com.example.trello.ViewModels.TrelloAuthService.TrelloAuthServiceViewModelFactory
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var trelloAuthServiceViewModelFactory : TrelloAuthServiceViewModelFactory

    lateinit var trelloAuthServiceViewModel: TrelloAuthServiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Вводим зависимости в нашем случае это VM для TrelloAuthService
        TrelloApplication.get(this).getAppComponent().inject(this)

        trelloAuthServiceViewModel = ViewModelProviders
            .of(this,trelloAuthServiceViewModelFactory)[TrelloAuthServiceViewModel::class.java]

    }
}
