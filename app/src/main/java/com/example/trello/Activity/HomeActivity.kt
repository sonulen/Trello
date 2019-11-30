package com.example.trello.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.trello.Application.TrelloApplication
import com.example.trello.DI.ActivityComponent.ActivityComponent
import com.example.trello.Fragments.Boards.Boards
import com.example.trello.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        // Для активити (если его еще нет) создадим ActivityComponent
        activityComponent = TrelloApplication.get(this).getActivityComponent()

        // Если загружаемся первый раз загружем фрагмент со всеми досками
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_placeholder,
                    Boards.newInstance(activityComponent.getTrelloClient())
                )
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Если activity уничтожают то и компонент тоже надо убить
        if (isFinishing) {
            TrelloApplication.get(this).destroyActivityComponent()
        }
    }



    // MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_logout -> {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    putExtra(EXTRAS_NAME_TO_REMOVE_ACCESS_TOKEN, "true")
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
