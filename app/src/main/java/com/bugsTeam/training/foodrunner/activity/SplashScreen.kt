package com.bugsTeam.training.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bugsTeam.training.foodrunner.R

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private lateinit var logo:ImageView
    private lateinit var appName:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logo=findViewById(R.id.app_logo)
        appName=findViewById(R.id.app_name)

        logo.animate().translationY(-100.0F).setDuration(1000).startDelay = 1000
        appName.animate().translationY(1400.0F).setDuration(1000).startDelay = 1000

        Handler().postDelayed({
                              val startApp= Intent(this@SplashScreen,LoginActivity::class.java)
            startActivity(startApp)
        },2000)

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}