package com.indevconsultancy.percentagecalculator.ui.settings

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
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
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.databinding.DialogChangeLanguageBinding
import com.indevconsultancy.percentagecalculator.databinding.FragmentSettingsBinding
import com.indevconsultancy.percentagecalculator.ui.about_me.AboutMe
import com.indevconsultancy.percentagecalculator.utils.CommonClass
import com.indevconsultancy.percentagecalculator.utils.SharedPrefHelper
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper
import java.util.Locale

class SettingsFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentSettingsBinding
    private var sharedPrefHelper: SharedPrefHelper? = null
    /**
     * language pref
     */
    private var editor: SharedPreferences.Editor? = null
    private var prefs: SharedPreferences? = null
    private var alertDialog: AlertDialog? = null

    private var rewardedAd: RewardedAd? = null
    /**
     * ads initialization
     */
    private var adRequest: AdRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefHelper = SharedPrefHelper(requireActivity())
        /**
         * Check language preferences when app open after destroyed
         */
        val inputLanguage = sharedPrefHelper!!.getString("inputLanguage", "")
        if (inputLanguage != "") {
            val locale = Locale(inputLanguage!!)
            Locale.setDefault(locale)
            val config: Configuration = requireActivity().baseContext.resources.configuration
            config.setLocale(locale)
            requireActivity().baseContext.resources.updateConfiguration(
                config,
                requireActivity().baseContext.resources.displayMetrics
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        binding= FragmentSettingsBinding.bind(view)

        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.settings))
        }
        // Configure AdMob to show test ads on this device
//        MobileAds.initialize(requireActivity()) { }
//        val configuration = RequestConfiguration.Builder()
//            .setTestDeviceIds(mutableListOf("5F113D5FD7F39144F9B6C130A19CFA3E")) // Replace with your actual test device ID
//            .build()
//        MobileAds.setRequestConfiguration(configuration)

        binding.rlDarkMode.setOnClickListener(this)
        binding.rlChangeLanguage.setOnClickListener(this)
        binding.rlChangeFont.setOnClickListener(this)
        binding.rlChangeTheme.setOnClickListener(this)
        binding.rlShareApp.setOnClickListener(this)
        binding.rlRateApp.setOnClickListener(this)
        binding.rlContactUs.setOnClickListener(this)
        binding.rlAboutMe.setOnClickListener(this)

        /**
         * initialization views
         */
        initViews()

        // Load the rewarded ad
        loadRewardedAd()

        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization()

        return  binding.root
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization() {
        MobileAds.initialize(requireActivity()){}
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

    private fun loadRewardedAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        RewardedAd.load(requireActivity(), getString(R.string.REWARDED_AD_UNIT_ID), adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(@NonNull ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d("RewardActivity", "Rewarded ad loaded")

                    if (rewardedAd != null) {
                        showRewardedAd();
                    } else {
                        Log.d(TAG, "Ad is not ready yet.")
                    }
                }

                override fun onAdFailedToLoad(@NonNull adError: LoadAdError) {
                    rewardedAd = null
                    Log.e(TAG, "Failed to load rewarded ad: " + adError.message)
                }
            })
    }
    private fun showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd!!.show(requireActivity()) { rewardItem: RewardItem ->
                // Handle the reward
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "User earned the reward: $rewardAmount $rewardType")
            }
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        prefs = context?.getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)

            binding.rlDarkMode.visibility=View.VISIBLE
            binding.rlChangeLanguage.visibility=View.VISIBLE
            binding.rlChangeFont.visibility=View.GONE
            binding.rlChangeTheme.visibility=View.GONE
            binding.rlShareApp.visibility=View.VISIBLE
            binding.rlRateApp.visibility=View.VISIBLE
            binding.rlContactUs.visibility=View.VISIBLE
            binding.rlAboutMe.visibility=View.VISIBLE

        val versionName = getAppVersionName()
        binding.tvAppVersion.text = ""+versionName
    }

    private fun getAppVersionName(): String {
        return try {
            val packageInfo: PackageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
        const val MY_PREFS_NAME = "MyPrefsFile"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rlDarkMode -> {
                (activity as MainActivity?)?.enableDarkMode()
            }
            R.id.rlChangeLanguage -> {
                showChangeLanguageDialog()
            }
            R.id.rlChangeFont -> {
                changeAppFont()
            }
            R.id.rlShareApp -> {
                shareApp()
            }
            R.id.rlRateApp -> {
                rateApp()
            }
            R.id.rlContactUs -> {
                contactUs()
            }
            R.id.rlAboutMe -> {
                loadFragmentOnActivity("AboutMe")
            }
            else -> {
                // Default action if needed
            }
    }
    }

    private fun contactUs() {
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
                                c.drawText(getString(R.string.arrow_point) + " ",0f,bottom - p.descent(),p)
                                p.style = orgStyle
                            }
                        }
                    }, 0, text.length, 0)
                    finalText = TextUtils.concat(finalText, spannableString)
                }
                val aboutBuilder = AlertDialog.Builder(requireActivity())
                aboutBuilder.setTitle(R.string.get_in_touch)
                    .setMessage(finalText.subSequence(0, finalText.length - 1))
                    .setPositiveButton(R.string._ok) { _, _ -> }
                    .create().show()
    }

    private fun rateApp() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireActivity().packageName}")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${requireActivity().packageName}")))
        }
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Hey check out my app at: https://play.google.com/store/apps/details?id=${requireActivity().packageName}")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun changeAppFont() {
        CommonClass.showCustomToast(requireActivity(),"Font Changed Successfully.")
    }

    override fun onResume() {
        super.onResume()
        /**
         * change theme mode Day/Night
         */
        initTheme()
    }

    /**
     * Sets up app theme based on SharedPreferences. Toggles between light and dark theme.
     */
    private fun initTheme() {
        val theme = ThemeHelper((requireActivity()))
        if (theme.loadNightMode()) {
            //set night mode colors
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor((requireActivity()), R.color.black_dark_mod))
        } else {
            //set day mode colors
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor((requireActivity()), R.color.white_dark_mod))
        }
    }

    private fun loadFragmentOnActivity(i: String) {
        when (i) {
            "AboutMe" -> {
                requireActivity().supportFragmentManager.beginTransaction().add(R.id.addFragmentOnActivity, AboutMe())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showChangeLanguageDialog() {
        val alertDialogBuilder = arrayOf(AlertDialog.Builder(requireActivity()))

        val dialogBinding: DialogChangeLanguageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.dialog_change_language,null,false)

        alertDialog = alertDialogBuilder[0].create()
        alertDialog!!.setView(dialogBinding.root)
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
                    editor = context?.getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)?.edit()
                    editor?.putString("language", "en")
                    editor?.apply()
                    //dismiss dialog
                    onFinishLanguageDialog(prefs!!.getString("language", ""))
                    alertDialog!!.dismiss()
                }

                R.id.rb_hindi -> {
                    editor = context?.getSharedPreferences(MY_PREFS_NAME, AppCompatActivity.MODE_PRIVATE)?.edit()
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

    private fun onFinishLanguageDialog(inputLanguage: String?) {
        sharedPrefHelper!!.setString("inputLanguage", inputLanguage)
        if (inputLanguage != "") {
            val locale = inputLanguage?.let { Locale(it) }
            if (locale != null) {
                Locale.setDefault(locale)
            }
            val config: Configuration = requireActivity().baseContext.resources.configuration
            config.setLocale(locale)
            requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

            restartActivity()
        }
    }

    private fun restartActivity() {
        val activity = requireActivity() // Get the activity hosting the fragment
        val intent = Intent(activity, activity::class.java) // Create an intent to restart the activity
        activity.finish() // Finish the current activity
        activity.startActivity(intent) // Start the activity again
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.percentage_calculator))
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                super.requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}