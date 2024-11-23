package com.indevconsultancy.percentagecalculator.ui.discount_percentage

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

class DiscountPercentage : Fragment() {
    private var etInitialValue: TextInputEditText? = null
    private var etDiscountPercentage: TextInputEditText? = null
    private var mtvDiscountValue: MaterialTextView? = null
    private var mtvValueAfterDiscount: MaterialTextView? = null
    var mMainLayout: ConstraintLayout? = null

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
        val view = inflater.inflate(R.layout.fragment_discount_percentage, container, false)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.discount_percentage))
        }
        initViews(view)
        initTheme()
        setPercentageDiscountCalculation()
        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization(view)
        return view
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization(view: View) {
        MobileAds.initialize((requireActivity())
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.percentage_calculator))
        }
    }

    private fun setPercentageDiscountCalculation() {
        /**
         * On percentage change calculate the value after discount
         */
        etDiscountPercentage!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * formula for calculate discount
                 * =>>initialValue * discountPercentage/100=result
                 * =>>after discount value=totalPrise - result
                 */
                if (etInitialValue!!.text.toString()
                        .trim { it <= ' ' } != "" && etDiscountPercentage!!.text.toString()
                        .trim { it <= ' ' } != ""
                ) {
                    val initialValue = java.lang.Double.valueOf(
                        etInitialValue!!.text.toString().trim { it <= ' ' })
                    val discountPercentage = java.lang.Double.valueOf(
                        etDiscountPercentage!!.text.toString().trim { it <= ' ' })
                    val result = (initialValue * discountPercentage) / 100
                    val afterDiscountValue = initialValue - result
                    mtvDiscountValue!!.text =
                        discountPercentage.toString() + " " + getString(R.string.percent_symbol)
                    mtvValueAfterDiscount!!.text =
                        afterDiscountValue.toString() + " " + getString(R.string.rupee_symbol)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        /**
         * On initial value change calculate the value after discount
         */
        etInitialValue!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * formula for calculate discount
                 * =>>initialValue * discountPercentage/100=result
                 * =>>after discount value=totalPrise - result
                 */
                if (etDiscountPercentage!!.text.toString()
                        .trim { it <= ' ' } != "" && etInitialValue!!.text.toString()
                        .trim { it <= ' ' } != ""
                ) {
                    val initialValue = java.lang.Double.valueOf(
                        etInitialValue!!.text.toString().trim { it <= ' ' })
                    val discountPercentage = java.lang.Double.valueOf(
                        etDiscountPercentage!!.text.toString().trim { it <= ' ' })
                    val result = (initialValue * discountPercentage) / 100
                    val afterDiscountValue = initialValue - result
                    mtvDiscountValue!!.text =
                        discountPercentage.toString() + " " + getString(R.string.percent_symbol)
                    mtvValueAfterDiscount!!.text =
                        afterDiscountValue.toString() + " " + getString(R.string.rupee_symbol)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun initViews(view: View) {
        etInitialValue = view.findViewById(R.id.tietInitialValue)
        etDiscountPercentage = view.findViewById(R.id.tietDiscountPercentage)
        mtvDiscountValue = view.findViewById(R.id.mtvDiscountValue)
        mtvValueAfterDiscount = view.findViewById(R.id.mtvValueAfterDiscount)
        mMainLayout = view.findViewById(R.id.mMainLayout)
    }

    /**
     * Set color depending on app theme.
     */
    private fun initTheme() {
        val theme = ThemeHelper((requireActivity()))
        if (theme.loadNightMode()) {
            //set night mode colors
            mMainLayout!!.setBackgroundColor(ContextCompat.getColor((requireActivity()), R.color.black_dark_mod))
        } else {
            //set day mode colors
            mMainLayout!!.setBackgroundColor(ContextCompat.getColor((requireActivity()), R.color.white_dark_mod))
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
        return (super.onOptionsItemSelected(menuItem))
    }
}