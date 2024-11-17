package com.indevconsultancy.percentagecalculator.ui.student_percentage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.indevconsultancy.percentagecalculator.MainActivity
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper

class StudentPercentage : Fragment() {
    private var etScoredMarks: TextInputEditText? = null
    private var etOutOfMarks: TextInputEditText? = null
    private var tilScoredMarks: TextInputLayout? = null
    private var tilOutOfMarks: TextInputLayout? = null
    private var mbCalculate: MaterialButton? = null
    private var mbClear: MaterialButton? = null
    private var mbShare: MaterialButton? = null
    private var mtvTotalPercentageScored: MaterialTextView? = null
    var mMainLayout: ConstraintLayout? = null
    private var result = 0.0

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
        val view = inflater.inflate(R.layout.fragment_student_percentage, container, false)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.student_percentage))
        }
        initViews(view)
        initTheme()
        setEnableDisable()
        setStudentPercentageCalculation()
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

    private fun setEnableDisable() {
        mbShare!!.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun setStudentPercentageCalculation() {
        mbCalculate!!.setOnClickListener {
            if (checkValidation()) {
                if (etScoredMarks!!.text.toString()
                        .trim { it <= ' ' } != "" && etOutOfMarks!!.text.toString()
                        .trim { it <= ' ' } != ""
                ) {
                    val scoredMarks: Int =
                        etScoredMarks!!.text.toString().trim { it <= ' ' }.toInt()
                    val outOfMarks: Int =
                        etOutOfMarks!!.text.toString().trim { it <= ' ' }.toInt()
                    result = java.lang.Double.valueOf(((scoredMarks * 100) / outOfMarks).toDouble())
                    if (result > 0.0 && result <= 39.0) {
                        mtvTotalPercentageScored!!.setTextColor(resources.getColor(R.color.color_red_google))
                        mtvTotalPercentageScored!!.text =
                            result.toString() + " " + getString(R.string.percent_symbol)
                        mbShare!!.isEnabled = true
                    } else if (result in 39.0..59.0) {
                        mtvTotalPercentageScored!!.setTextColor(resources.getColor(R.color.color_yellow_google))
                        mtvTotalPercentageScored!!.text =
                            result.toString() + " " + getString(R.string.percent_symbol)
                        mbShare!!.isEnabled = true
                    } else if (result in 59.0..74.0) {
                        mtvTotalPercentageScored!!.setTextColor(resources.getColor(R.color.color_blue_google))
                        mtvTotalPercentageScored!!.text =
                            result.toString() + " " + getString(R.string.percent_symbol)
                        mbShare!!.isEnabled = true
                    } else if (result in 74.0..100.0) {
                        mtvTotalPercentageScored!!.setTextColor(resources.getColor(R.color.color_green_google))
                        mtvTotalPercentageScored!!.text =
                            result.toString() + " " + getString(R.string.percent_symbol)
                        mbShare!!.isEnabled = true
                    }
                    //hide input-dialog
                    try {
                        val imm: InputMethodManager =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(
                            requireActivity().currentFocus!!.windowToken, 0
                        )
                    } catch (e: Exception) {
                        // TODO: handle exception
                    }
                }
            }
        }
        mbClear!!.setOnClickListener {
            etScoredMarks!!.text = null
            etOutOfMarks!!.text = null
            mtvTotalPercentageScored!!.setTextColor(resources.getColor(R.color.black))
            mtvTotalPercentageScored!!.text = getString(R.string._00_00)
            setEnableDisable()
        }
        mbShare!!.setOnClickListener {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val shareBody: String =
                "Total percentage scored in exam." + "\n" + result + " " + getString(R.string.percent_symbol)
            /*The type of the content is text, obviously.*/intent.type = "text/plain"
            /*Applying information Subject and Body.*/intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.total_percentage)
        )
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/startActivity(
            Intent.createChooser(
                intent,
                getString(R.string.percentage_calculator)
            )
        )
        }
    }

    private fun initViews(view: View) {
        etScoredMarks = view.findViewById(R.id.tietScoredMarks)
        etOutOfMarks = view.findViewById(R.id.tietOutOfMarks)
        tilScoredMarks = view.findViewById(R.id.tilScoredMarks)
        tilOutOfMarks = view.findViewById(R.id.tilOutOfMarks)
        mbCalculate = view.findViewById(R.id.mbCalculate)
        mbClear = view.findViewById(R.id.mbClear)
        mbShare = view.findViewById(R.id.mbShare)
        mtvTotalPercentageScored = view.findViewById(R.id.mtvTotalPercentageScored)
        mMainLayout = view.findViewById(R.id.mMainLayout)
    }

    private fun checkValidation(): Boolean {
        if (etScoredMarks!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            tilScoredMarks!!.error = getString(R.string.please_enter_scored_marks)
            return false
        } else {
            tilScoredMarks!!.error = null
        }
        if (etOutOfMarks!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            tilOutOfMarks!!.error = getString(R.string.please_enter_out_of_marks)
            return false
        } else {
            tilOutOfMarks!!.error = null
        }
        return true
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
                super.requireActivity().onBackPressed()
                return true
            }
        }
        return (super.onOptionsItemSelected(menuItem))
    }
}