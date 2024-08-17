package com.indevconsultancy.percentagecalculator

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.indevconsultancy.percentagecalculator.databinding.ActivityMainBinding
import com.indevconsultancy.percentagecalculator.databinding.DialogChangeLanguageBinding
import com.indevconsultancy.percentagecalculator.databinding.DialogForAppUpdateBinding
import com.indevconsultancy.percentagecalculator.fragments.AboutMe
import com.indevconsultancy.percentagecalculator.fragments.AgeCalculator
import com.indevconsultancy.percentagecalculator.fragments.BmiCalculator
import com.indevconsultancy.percentagecalculator.fragments.DiscountPercentage
import com.indevconsultancy.percentagecalculator.fragments.StudentPercentage
import com.indevconsultancy.percentagecalculator.utils.SharedPrefHelper
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    /**
     * checking for update app
     */
    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private var sharedPrefHelper: SharedPrefHelper? = null
    var context: Context = this
    private var theme: ThemeHelper? = null

    /**
     * ads initialization
     */
    private var adRequest: AdRequest? = null

    /**
     * language pref
     */
    private var editor: Editor? = null
    private var prefs: SharedPreferences? = null
    private var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefHelper = SharedPrefHelper(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                Log.e(
                    "Token Error:::::::",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e("Generate Token:::::::", token!!)
        }
        val intent = intent
        if (intent != null && intent.extras != null) {
            val title = intent.getStringExtra("title")
            val body = intent.getStringExtra("body")
            val extraData = intent.getStringExtra("extra_data")

            Log.e(TAG, "push json data: $title\n$body\n$extraData")
        }
        /**
         * Check language preferences when app open after destroyed
         */
        val inputLanguage = sharedPrefHelper!!.getString("inputLanguage", "")
        if (inputLanguage != "") {
            val locale = Locale(inputLanguage!!)
            Locale.setDefault(locale)
            val config: Configuration = baseContext.resources.configuration
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
        }
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setActionBarTitle(getString(R.string.percentage_calculator))
        /**
         * initialization views
         */
        initViews()
        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization()
        /**
         * checking for update aap
         */
        checkingForAppUpdate()
        /**
         * change theme mode Day/Night
         */
        initTheme()
        binding.cvDiscountPercentage.setOnClickListener { loadFragmentOnActivity(0) }
        binding.cvStudentPercentage.setOnClickListener { loadFragmentOnActivity(1) }
        binding.cvAgeCalculator.setOnClickListener { loadFragmentOnActivity(4) }
        binding.cvBMICalculator.setOnClickListener { loadFragmentOnActivity(5) }
    }

    private fun initViews() {
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
    }

    private fun checkingForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate(context, appUpdateManager)
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener()
            } else {
                /**
                 * Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: "
                 * + state.installStatus(), Toast.LENGTH_LONG).show();
                 */
            }
        }
        appUpdateManager!!.registerListener(installStateUpdatedListener!!)
        checkUpdate()
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization() {
        MobileAds.initialize(this
        )
        /**
         * Toast.makeText(MainActivity.this, ""+initializationStatus, Toast.LENGTH_SHORT).show();
         */
        /**
         * Toast.makeText(MainActivity.this, ""+initializationStatus, Toast.LENGTH_SHORT).show();
         */
        {
            /**
             * Toast.makeText(MainActivity.this, ""+initializationStatus, Toast.LENGTH_SHORT).show();
             */
        }
        adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest!!)
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                /**
                 * Code to be executed when an ad finishes loading.
                 */
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                /**
                 * Code to be executed when an ad request fails.
                 */
            }

            override fun onAdOpened() {
                /***
                 * Code to be executed when an ad opens an overlay
                 * that covers the screen.
                 */
            }

            override fun onAdClicked() {
                /**
                 * Code to be executed when the user
                 * clicks on an ad.
                 */
            }

            override fun onAdClosed() {
                /**
                 * Code to be executed when the user is about to return to
                 * the app after tapping on an ad.
                 */
            }
        }
    }

    private fun loadFragmentOnActivity(count: Int) {
        when (count) {
            0 -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, DiscountPercentage())
                    .addToBackStack(null)
                    .commit()
            }
            1 -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, StudentPercentage())
                    .addToBackStack(null)
                    .commit()
            }
            3 -> {
                supportFragmentManager.beginTransaction().add(R.id.addFragmentOnActivity, AboutMe())
                    .addToBackStack(null)
                    .commit()
            }
            4 -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, AgeCalculator())
                    .addToBackStack(null)
                    .commit()
            }
            5 -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, BmiCalculator())
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
                showChangeLanguageDialog()
            }
        }
    }

    private fun showChangeLanguageDialog() {
        val alertDialogBuilder = arrayOf(AlertDialog.Builder(context))

        val dialogBinding: DialogChangeLanguageBinding= DataBindingUtil.inflate(
            LayoutInflater.from(this@MainActivity),
            R.layout.dialog_change_language,
            null,
            false
        )

        alertDialog = alertDialogBuilder[0].create()
        alertDialog!!.setView(dialogBinding.root)
//        alertDialog!!.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.window?.setGravity(Gravity.CENTER)

        /**
         * Check previous selected language
         */
        val langCode = prefs!!.getString("language", "")
        if (langCode.equals("en", ignoreCase = true)) {
            dialogBinding.rbEnglish.isChecked = true
        } else if (langCode.equals("hi", ignoreCase = true)) {
            dialogBinding.rbHindi.isChecked = true
        }
        dialogBinding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_english -> {
                    editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                    editor?.putString("language", "en")
                    editor?.apply()
                    //dismiss dialog
                    onFinishLanguageDialog(prefs!!.getString("language", ""))
                    alertDialog!!.dismiss()
                }

                R.id.rb_hindi -> {
                    editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                    editor?.putString("language", "hi")
                    editor?.apply()
                    //dismiss dialog
                    onFinishLanguageDialog(prefs!!.getString("language", ""))
                    alertDialog!!.dismiss()
                }
            }
        }
        alertDialog?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }

            R.id.themes -> {
                when (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        theme!!.setNightModeState(false)
                        makeToast("Light theme enabled")
                    }

                    Configuration.UI_MODE_NIGHT_NO -> {
                        theme!!.setNightModeState(true)
                        makeToast("Dark theme enabled")
                    }
                }
                restart()
                true
            }

            R.id.share_app -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=$packageName"
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
                true
            }

            R.id.rate_app -> {
                val appPackageName: String =
                    packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
                true
            }

            R.id.contact_us -> {
                val about = resources.getStringArray(R.array.contact_us)
                var finalText: CharSequence = ""
                for (message in about) {
                    val text = message.trim { it <= ' ' } + "."
                    val spannableString = SpannableString(text.trimIndent())
                    spannableString.setSpan(object : LeadingMarginSpan {
                        override fun getLeadingMargin(first: Boolean): Int {
                            return getString(R.string.arrow_point).length * 50
                        }

                        override fun drawLeadingMargin(
                            c: Canvas,
                            p: Paint,
                            x: Int,
                            dir: Int,
                            top: Int,
                            baseline: Int,
                            bottom: Int,
                            text: CharSequence,
                            start: Int,
                            end: Int,
                            first: Boolean,
                            layout: Layout
                        ) {
                            if (first) {
                                val orgStyle = p.style
                                p.style = Paint.Style.FILL
                                c.drawText(
                                    getString(R.string.arrow_point) + " ",
                                    0f,
                                    bottom - p.descent(),
                                    p
                                )
                                p.style = orgStyle
                            }
                        }
                    }, 0, text.length, 0)
                    finalText = TextUtils.concat(finalText, spannableString)
                }
                val aboutBuilder = AlertDialog.Builder(this)
                aboutBuilder.setTitle(R.string.get_in_touch)
                    .setMessage(finalText.subSequence(0, finalText.length - 1))
                    .setPositiveButton(R.string._ok, null)
                    .create().show()
                true
            }

            R.id.change_language -> {
                loadFragmentOnActivity(2)
                true
            }

            R.id.change_font ->                 //changeAppFont();
                true

            R.id.about_me -> {
                loadFragmentOnActivity(3)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Sets up app theme based on SharedPreferences. Toggles between light and dark theme.
     */
    private fun initTheme() {
        theme = ThemeHelper(this)
        if (theme!!.loadNightMode()) {
            //set to night mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            //set to day mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * Create default layout for toast alerts.
     *
     * @param message -- The message to be displayed in the toast.
     */
    private fun makeToast(message: String?) {
        val alert: Toast = Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
        alert.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
        alert.show()
    }

    /**
     * Restarts activity when called.
     */
    private fun restart() {
        finish()
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            when (resultCode) {
                RESULT_CANCELED -> {
                    Log.d(TAG, "onActivityResult: Update canceled by user! Result Code: $resultCode")
                }
                RESULT_OK -> {
                    Log.d(TAG, "onActivityResult: Update success! Result Code: $resultCode")
                }
                else -> {
                    checkUpdate()
                }
            }
        }
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                startUpdateFlow(appUpdateInfo)
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate(context, appUpdateManager)
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                this,
                FLEXIBLE_APP_UPDATE_REQ_CODE
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager!!.unregisterListener(installStateUpdatedListener!!)
        }
    }

    override fun onStop() {
        super.onStop()
        removeInstallStateUpdateListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (alertDialog != null) {
                alertDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }

    private fun onFinishLanguageDialog(inputLanguage: String?) {
        sharedPrefHelper!!.setString("inputLanguage", inputLanguage)
        if (inputLanguage != "") {
            val locale = inputLanguage?.let { Locale(it) }
            if (locale != null) {
                Locale.setDefault(locale)
            }
            val config: Configuration = baseContext.resources.configuration
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(
                config,
                baseContext.resources.displayMetrics
            )
            finish()
            startActivity(Intent(this@MainActivity, this@MainActivity.javaClass))
        }
    }

    companion object {
        private const val TAG = "MainActivity>>"
        private const val FLEXIBLE_APP_UPDATE_REQ_CODE = 123
        const val MY_PREFS_NAME = "MyPrefsFile"
        private fun popupSnackBarForCompleteUpdate(
            context: Context, appUpdateManager: AppUpdateManager?
        ) {
            val alertDialog: AlertDialog?
            val alertDialogBuilder = arrayOf(AlertDialog.Builder(context))
            val dialogBinding: DialogForAppUpdateBinding= DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_for_app_update,
                null,
                false
            )

            alertDialog = alertDialogBuilder[0].create()
            alertDialog.setView(dialogBinding.root)
//            alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.window?.setGravity(Gravity.CENTER)

            val finalAlertDialog: AlertDialog = alertDialog
            dialogBinding.btnLater.setOnClickListener { finalAlertDialog.dismiss() }
            dialogBinding.btnInstallNow.setOnClickListener {
                finalAlertDialog.dismiss()
                appUpdateManager?.completeUpdate()
            }
            alertDialog.show()
        }
    }
}