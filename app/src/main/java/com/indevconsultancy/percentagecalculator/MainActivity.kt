package com.indevconsultancy.percentagecalculator

import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
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
import com.indevconsultancy.percentagecalculator.databinding.DialogForAppUpdateBinding
import com.indevconsultancy.percentagecalculator.ui.dashboard.DashboardFragment
import com.indevconsultancy.percentagecalculator.ui.settings.SettingsFragment
import com.indevconsultancy.percentagecalculator.utils.SharedPrefHelper
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

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

        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setActionBarTitle(getString(R.string.percentage_calculator))

        /**
         * change theme mode Day/Night
         */
        initTheme()

        /**
         * checking for update aap
         */
        checkingForAppUpdate()

        callDashboardPage()
    }

    fun enableDarkMode() {
        when (this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                theme!!.setNightModeState(false)
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                theme!!.setNightModeState(true)
            }
        }
        restartActivity()
    }

    /**
     * Restarts activity when called.
     */
    private fun restartActivity() {
        val intent = Intent(this@MainActivity, this@MainActivity::class.java)
        finish() // Finish the current activity
        startActivity(intent) // Start the activity again
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

    private fun callDashboardPage() {
        supportFragmentManager.beginTransaction()
            .add(R.id.addFragmentOnActivity, DashboardFragment())
            .commit()
    }

    private fun checkingForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate(context, appUpdateManager)
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener()
            } else {
                Log.d(TAG, "InstallStateUpdatedListener: "+state.installStatus())
            }
        }
        appUpdateManager!!.registerListener(installStateUpdatedListener!!)
        checkUpdate()
    }
    private fun loadFragmentOnActivity(count: Int) {
        when (count) {
            1 -> {
                supportFragmentManager.beginTransaction().add(R.id.addFragmentOnActivity, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val currentFragment=supportFragmentManager.findFragmentById(R.id.addFragmentOnActivity)
        return if(currentFragment is DashboardFragment){
            menuInflater.inflate(R.menu.main_menu, menu)
            true
        }else{
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            R.id.settings -> {
                loadFragmentOnActivity(1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    fun setActionBarTitle(title: String?) {
        this.invalidateOptionsMenu()
        supportActionBar!!.title = title
    }

    companion object {
        private const val TAG = "MainActivity>>"
        private const val FLEXIBLE_APP_UPDATE_REQ_CODE = 123

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