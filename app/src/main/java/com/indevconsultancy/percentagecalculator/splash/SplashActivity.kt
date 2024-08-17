package com.indevconsultancy.percentagecalculator.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.viewanimator.ViewAnimator
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R

class SplashActivity : AppCompatActivity() {
    /*normal widgets*/
    private val SPLASH_DISPLAY_LENGTH = 5000
    private val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_activitity)
        val preferences = getSharedPreferences("PercentageCalculator", MODE_PRIVATE)
        // Get value from shared preferences,
        // if null (app is run first time) the default value (second argument) is returned
        val isFIrstRun = preferences.getBoolean("isFirstRun", true)
        if (isFIrstRun) {
            // set isFirstRun to false
            preferences.edit().putBoolean("isFirstRun", false).apply()
            // launch splash screen activity
            callNextActivity()
        } else {
            // Launch other activity
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun callNextActivity() {
        startAnimationForSplashImage()
        Handler().postDelayed({
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    private fun startAnimationForSplashImage() {
        val ivSplash = findViewById<ImageView>(R.id.ivSplash)
        ViewAnimator
            .animate(ivSplash)
            .translationY(-1000f, 0f)
            .alpha(0f, 1f)
            .decelerate()
            .duration(2000)
            .thenAnimate(ivSplash)
            .scale(1f, 0.5f, 1f)
            .accelerate()
            .duration(1000)
            .start()
    }
}