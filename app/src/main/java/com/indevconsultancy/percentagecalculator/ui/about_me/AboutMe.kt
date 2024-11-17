package com.indevconsultancy.percentagecalculator.ui.about_me

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

class AboutMe : Fragment() {
    var mMainLayout: ConstraintLayout? = null
    private var gitHub: ImageView? = null
    private var linkedIn: ImageView? = null
    private var youTube: ImageView? = null

    private var rewardedAd: RewardedAd? = null
    /**
     * ads initialization
     */
    private var mAdView: AdView? = null
    private var adRequest: AdRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_me, container, false)

        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.about_me))
        }
        // Configure AdMob to show test ads on this device
//        MobileAds.initialize(requireActivity()) { }
//        val configuration = RequestConfiguration.Builder()
//            .setTestDeviceIds(mutableListOf("5F113D5FD7F39144F9B6C130A19CFA3E")) // Replace with your actual test device ID
//            .build()
//        MobileAds.setRequestConfiguration(configuration)

        initViews(view)

        setOpenSocialLink()

        // Load the rewarded ad
        loadRewardedAd()
        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization(view)
        return view
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization(view: View) {
        MobileAds.initialize(
            requireActivity()
        )
        {

        }
        mAdView = view.findViewById(R.id.adView)
        adRequest = AdRequest.Builder().build()
        mAdView?.loadAd(adRequest!!)
        mAdView?.adListener = object : AdListener() {
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

    companion object {
        private const val TAG = "AboutMe"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.settings))
        }
    }

    private fun setOpenSocialLink() {
        gitHub!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://github.com/vimalku637")
            startActivity(intent)
        }
        linkedIn!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/vimal-kumar-91383992/")
            startActivity(intent)
        }
        youTube!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/channel/UCqynb92o-6OCGrkV0Wl3RPg")
            startActivity(intent)
        }
    }

    private fun initViews(view: View) {
        mMainLayout = view.findViewById(R.id.mMainLayout)
        gitHub = view.findViewById(R.id.gitHub)
        linkedIn = view.findViewById(R.id.linkedIn)
        youTube = view.findViewById(R.id.youTube)
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
        val theme = ThemeHelper(requireActivity())
        if (theme.loadNightMode()) {
            //set night mode colors
            mMainLayout!!.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.black_dark_mod))
        } else {
            //set day mode colors
            mMainLayout!!.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white_dark_mod))
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