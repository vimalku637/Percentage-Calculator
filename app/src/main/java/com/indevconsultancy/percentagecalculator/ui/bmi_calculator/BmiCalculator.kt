package com.indevconsultancy.percentagecalculator.ui.bmi_calculator

import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper
import java.math.RoundingMode
import java.text.DecimalFormat

class BmiCalculator : Fragment() {
    var mMainLayout: ConstraintLayout? = null
    private var etWeight: TextInputEditText? = null
    private var etHeight: TextInputEditText? = null
    private var mtvBMICalculation: MaterialTextView? = null
    private var mbShare: MaterialButton? = null
    private var mbClear: MaterialButton? = null
    private var result = 0.0
    private var shareBMI = ""

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
        val view = inflater.inflate(R.layout.fragment_bmi_calculator, container, false)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.bmi_calculator))
        }
        initViews(view)
        initTheme()
        setEnableDisable()
        setBMICalculations()
        /**
         * Ad-Mob for show ads in app
         */
        adsInitialization(view)
        return view
    }

    @SuppressLint("MissingPermission")
    private fun adsInitialization(view: View) {
        MobileAds.initialize(
            (requireActivity())
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

    private fun setEnableDisable() {
        mbShare!!.isEnabled = false
        mbClear!!.isEnabled = false
    }

    private fun setBMICalculations() {
        /**
         * on weight change calculate the BMI
         */
        etWeight!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /**
                 * formula for calculate BMI
                 * =>>(weight / height / height) * 10,000=result
                 */
                if (etWeight!!.text.toString()
                        .trim { it <= ' ' } != "" && etHeight!!.text.toString()
                        .trim { it <= ' ' } != ""
                ) {
                    val weight =
                        java.lang.Double.valueOf(etWeight!!.text.toString().trim { it <= ' ' })
                    val height =
                        java.lang.Double.valueOf(etHeight!!.text.toString().trim { it <= ' ' })
                    val resultBMI = ((weight / height / height) * 10000)
                    df.roundingMode = RoundingMode.DOWN
                    result = java.lang.Double.valueOf(df.format(resultBMI))
                    if (result > 0.0 && result <= 18.5) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_blue_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.underweight)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    } else if (result in 18.5..25.0) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_green_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.normal)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    } else if (result > 25.0) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_red_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.overweight)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    }
                    //mtvBMICalculation.setText(String.format("%.2f", resultBMI));

                    /*BigDecimal bd = new BigDecimal(resultBMI).setScale(2, RoundingMode.HALF_UP);
                    double newInput = bd.doubleValue();
                    mtvBMICalculation.setText(newInput+"");*/
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        /**
         * on height change calculate the BMI
         */
        etHeight!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                /**
                 * formula for calculate BMI
                 * =>>(weight / height / height) * 10,000=result
                 */
                if (etWeight!!.text.toString()
                        .trim { it <= ' ' } != "" && etHeight!!.text.toString()
                        .trim { it <= ' ' } != ""
                ) {
                    val weight =
                        java.lang.Double.valueOf(etWeight!!.text.toString().trim { it <= ' ' })
                    val height =
                        java.lang.Double.valueOf(etHeight!!.text.toString().trim { it <= ' ' })
                    val resultBMI = ((weight / height / height) * 10000)
                    df.roundingMode = RoundingMode.DOWN
                    result = java.lang.Double.valueOf(df.format(resultBMI))
                    if (result > 0.0 && result <= 18.5) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_blue_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.underweight)
                        shareBMI = result.toString() + "" + "\n" + getString(R.string.underweight)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    } else if (result in 18.5..25.0) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_green_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.normal)
                        shareBMI = result.toString() + "" + "\n" + getString(R.string.normal)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    } else if (result > 25.0) {
                        mtvBMICalculation!!.setTextColor(resources.getColor(R.color.color_red_google))
                        mtvBMICalculation!!.text =
                            result.toString() + "" + "\n" + getString(R.string.overweight)
                        shareBMI = result.toString() + "" + "\n" + getString(R.string.overweight)
                        mbShare!!.isEnabled = true
                        mbClear!!.isEnabled = true
                    }
                    //mtvBMICalculation.setText(String.format("%.2f", resultBMI));

                    /*BigDecimal bd = new BigDecimal(resultBMI).setScale(2, RoundingMode.HALF_UP);
                    double newInput = bd.doubleValue();
                    mtvBMICalculation.setText(newInput+"");*/
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mbShare!!.setOnClickListener {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val shareBody = "Total BMI calculated.\n$shareBMI"
            /*The type of the content is text, obviously.*/intent.type = "text/plain"
            /*Applying information Subject and Body.*/intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.bmi)
        )
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.percentage_calculator)
            )
        )
        }
        mbClear!!.setOnClickListener {
            etWeight!!.text = null
            etHeight!!.text = null
            mtvBMICalculation!!.setTextColor(resources.getColor(R.color.black))
            mtvBMICalculation!!.text = getString(R.string._00_00__)
            setEnableDisable()
        }
    }

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

    private fun initViews(view: View) {
        mMainLayout = view.findViewById(R.id.mMainLayout)
        etWeight = view.findViewById(R.id.tietWeight)
        etHeight = view.findViewById(R.id.tietHeight)
        mtvBMICalculation = view.findViewById(R.id.mtvBMICalculation)
        mbShare = view.findViewById(R.id.mbShare)
        mbClear = view.findViewById(R.id.mbClear)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                super.requireActivity().onBackPressed()
                return true
            }
        }
        return (super.onOptionsItemSelected(menuItem))
    }

    companion object {
        private val df = DecimalFormat("0.0")
    }
}