package com.indevconsultancy.percentagecalculator.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.databinding.ActivitySplashActivitityBinding
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashActivitityBinding
    private val SPLASH_DISPLAY_LENGTH = 1500
    private val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash_activitity)
//        val preferences = getSharedPreferences("PercentageCalculator", MODE_PRIVATE)
//        // Get value from shared preferences,
//        // if null (app is run first time) the default value (second argument) is returned
//        val isFirstRun = preferences.getBoolean("isFirstRun", true)
//        if (isFirstRun) {
//            // set isFirstRun to false
//            preferences.edit().putBoolean("isFirstRun", false).apply()
//            // launch splash screen activity
//            callNextActivity()
//        } else {
//            // Launch other activity
//            val intent = Intent(context, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
        // launch splash screen activity
        callNextActivity()
    }

    private fun callNextActivity() {
        Handler().postDelayed({
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    override fun onResume() {
        super.onResume()
        /**
         * change theme mode Day/Night
         */
        initTheme()
    }

    /**
     * Set color depending on app theme.
     */
    private fun initTheme() {
        val theme = ThemeHelper(this)
        if (theme.loadNightMode()) {
            //set night mode colors
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black_dark_mod))
        } else {
            //set day mode colors
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_dark_mod))
        }
    }
}