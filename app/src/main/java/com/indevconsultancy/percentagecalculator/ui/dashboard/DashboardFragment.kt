package com.indevconsultancy.percentagecalculator.ui.dashboard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.databinding.FragmentDashboardBinding
import com.indevconsultancy.percentagecalculator.ui.age_calculator.AgeCalculator
import com.indevconsultancy.percentagecalculator.ui.bmi_calculator.BmiCalculator
import com.indevconsultancy.percentagecalculator.ui.discount_percentage.DiscountPercentage
import com.indevconsultancy.percentagecalculator.ui.student_percentage.StudentPercentage
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

//    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd?=null

    /**
     * ads initialization
     */
    private var adRequest: AdRequest? = null
    private var preferences: SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_dashboard, container, false)
        binding=FragmentDashboardBinding.bind(view)

        preferences = requireActivity().getSharedPreferences("PercentageCalculator", AppCompatActivity.MODE_PRIVATE)
        preferences?.edit()?.putBoolean("isSignInSignUp", false)?.apply()

        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.percentage_calculator))
        }
        // Configure AdMob to show test ads on this device
        MobileAds.initialize(requireActivity()) { }
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(mutableListOf("5F113D5FD7F39144F9B6C130A19CFA3E")) // Replace with your actual test device ID
            .build()
        MobileAds.setRequestConfiguration(configuration)

        binding.cvDiscountPercentage.setOnClickListener { loadFragmentOnActivity(0) }
        binding.cvStudentPercentage.setOnClickListener { loadFragmentOnActivity(1) }
        binding.cvAgeCalculator.setOnClickListener { loadFragmentOnActivity(2) }
        binding.cvBMICalculator.setOnClickListener { loadFragmentOnActivity(3) }

        // Load the rewarded ad
//        loadRewardedAd()

        // Load the Interstitial Ad
        loadInterstitialAd()
        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization()

        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization() {
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

//    private fun loadRewardedAd() {
//        val adRequest: AdRequest = AdRequest.Builder().build()
//        RewardedAd.load(requireActivity(), getString(R.string.REWARDED_AD_UNIT_ID), adRequest,
//            object : RewardedAdLoadCallback() {
//                override fun onAdLoaded(@NonNull ad: RewardedAd) {
//                    rewardedAd = ad
//                    Log.d("RewardActivity", "Rewarded ad loaded")
//
//                    if (rewardedAd != null) {
//                        showRewardedAd();
//                    } else {
//                        Log.d(TAG, "Ad is not ready yet.")
//                    }
//                }
//
//                override fun onAdFailedToLoad(@NonNull adError: LoadAdError) {
//                    rewardedAd = null
//                    Log.e(TAG, "Failed to load rewarded ad: " + adError.message)
//                }
//            })
//    }
//    private fun showRewardedAd() {
//        if (rewardedAd != null) {
//            rewardedAd!!.show(requireActivity()) { rewardItem: RewardItem ->
//                // Handle the reward
//                val rewardAmount = rewardItem.amount
//                val rewardType = rewardItem.type
//                Log.d(TAG, "User earned the reward: $rewardAmount $rewardType")
//            }
//        } else {
//            Log.d(TAG, "The rewarded ad wasn't ready yet.")
//        }
//    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireActivity(),
            getString(R.string.INTERSTITIAL_AD_UNIT_ID), // Use test Ad Unit ID for testing
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad

                    showInterstitialAd()
//                    setupAdCallbacks()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.d(TAG, "onAdFailedToLoad: "+"Ad failed to load")
                }
            }
        )
    }

    private fun setupAdCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent: "+"Ad dismissed")
                // Reload the ad after it's closed
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent: "+"Ad failed to show")
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null // Ad is shown, so clear the reference
            }
        }
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(requireActivity())
        } else {
            Log.d(TAG, "showInterstitialAd: "+"Ad not ready yet, proceeding without ad")
        }
    }

    private fun loadFragmentOnActivity(count: Int) {
        when (count) {
            0 -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, DiscountPercentage())
                    .addToBackStack(null)
                    .commit()
            }
            1 -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, StudentPercentage())
                    .addToBackStack(null)
                    .commit()
            }
            2 -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, AgeCalculator())
                    .addToBackStack(null)
                    .commit()
            }
            3 -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.addFragmentOnActivity, BmiCalculator())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    companion object {
        private const val TAG = "DashboardFragment"
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
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.black_dark_mod))
        } else {
            //set day mode colors
            binding.mMainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white_dark_mod))
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
//                super.requireActivity().onBackPressed()
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}