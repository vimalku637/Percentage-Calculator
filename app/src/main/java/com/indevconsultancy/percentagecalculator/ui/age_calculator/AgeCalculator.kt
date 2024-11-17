package com.indevconsultancy.percentagecalculator.ui.age_calculator

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
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
import com.indevconsultancy.percentagecalculator.utils.CommonClass.currentDateTime
import com.indevconsultancy.percentagecalculator.utils.ThemeHelper
import java.text.SimpleDateFormat
import java.util.Calendar

class AgeCalculator : Fragment() {
    var mMainLayout: ConstraintLayout? = null
    private var etDateOfBirthday: TextInputEditText? = null
    private var etTodayDate: TextInputEditText? = null
    private var mtvAgeDetails: MaterialTextView? = null
    private var mtvNextBirthdayDetails: MaterialTextView? = null
    private var mtvYears: MaterialTextView? = null
    private var mtvMonths: MaterialTextView? = null
    private var mtvWeeks: MaterialTextView? = null
    private var mtvDays: MaterialTextView? = null
    private var mtvHours: MaterialTextView? = null
    private var mtvMinutes: MaterialTextView? = null
    private var mbShare: MaterialButton? = null
    private var shareAGE = ""

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
        val view = inflater.inflate(R.layout.fragment_age_calculator, container, false)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.age_calculator))
        }
        initViews(view)
        initTheme()
        setEnableDisable()
        setClickListeners()
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity != null) {
            (activity as MainActivity?)!!.setActionBarTitle(getString(R.string.percentage_calculator))
        }
    }

    private fun setEnableDisable() {
        mbShare!!.isEnabled = false
    }

    private fun setClickListeners() {
        etTodayDate!!.setText(currentDateTime)
        etDateOfBirthday!!.setOnClickListener {
            openDatePickerDialog(
                etDateOfBirthday,
                getString(R.string.date_of_birthday)
            )
        }
        etTodayDate!!.setOnClickListener {
            openDatePickerDialog(
                etTodayDate,
                getString(R.string.today_date)
            )
        }
        mbShare!!.setOnClickListener {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val shareBody = "Total age calculated.\n$shareAGE"
            /*The type of the content is text, obviously.*/intent.type = "text/plain"
            /*Applying information Subject and Body.*/intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.age)
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

    // Create and show a DatePickerDialog when click button.
    @SuppressLint("SetTextI18n")
    private fun openDatePickerDialog(text: TextInputEditText?, title: String) {
        // Create a new OnDateSetListener instance. This listener will be invoked when user click ok button in DatePickerDialog.
        val onDateSetListener =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val hours = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                val minutes = Calendar.getInstance()[Calendar.MINUTE]
                val seconds = Calendar.getInstance()[Calendar.SECOND]
                val stringBuffer = StringBuffer()
                stringBuffer.append(hours)
                stringBuffer.append(":")
                stringBuffer.append(minutes)
                stringBuffer.append(":")
                stringBuffer.append(seconds)
                text!!.setText(
                    String.format(
                        "%02d-%02d-%04d",
                        dayOfMonth,
                        monthOfYear + 1,
                        year
                    ) + " " + stringBuffer
                )
                if (title == getString(R.string.today_date)) {
                    if (etDateOfBirthday!!.text.toString() != "") {
                        shareAGE = calculateAge(
                            etDateOfBirthday!!.text.toString(),
                            etTodayDate!!.text.toString()
                        )
                        mtvAgeDetails!!.text = shareAGE
                        mbShare!!.isEnabled = true
                    }
                } else {
                    if (etTodayDate!!.text.toString() != "") {
                        shareAGE = calculateAge(
                            etDateOfBirthday!!.text.toString(),
                            etTodayDate!!.text.toString()
                        )
                        mtvAgeDetails!!.text = shareAGE
                        mbShare!!.isEnabled = true
                    }
                }
            }
        // Get current year, month and day.
        val now = Calendar.getInstance()
        val year = now[Calendar.YEAR]
        val month = now[Calendar.MONTH]
        val day = now[Calendar.DAY_OF_MONTH]
        // Create the new DatePickerDialog instance.
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            android.R.style.Theme_Holo_Dialog,
            onDateSetListener,
            year,
            month,
            day
        )
        // Set dialog icon and title.
        datePickerDialog.setTitle(title)
        // Popup the dialog.
        datePickerDialog.show()
    }

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

    private fun initViews(view: View) {
        mMainLayout = view.findViewById(R.id.mMainLayout)
        etDateOfBirthday = view.findViewById(R.id.tietDateOfBirthday)
        etTodayDate = view.findViewById(R.id.tietTodayDate)
        mtvAgeDetails = view.findViewById(R.id.mtvAgeDetails)
        mtvNextBirthdayDetails = view.findViewById(R.id.mtvNextBirthdayDetails)
        mtvYears = view.findViewById(R.id.mtvYears)
        mtvMonths = view.findViewById(R.id.mtvMonths)
        mtvWeeks = view.findViewById(R.id.mtvWeeks)
        mtvDays = view.findViewById(R.id.mtvDays)
        mtvHours = view.findViewById(R.id.mtvHours)
        mtvMinutes = view.findViewById(R.id.mtvMinutes)
        mbShare = view.findViewById(R.id.mbShare)
    }

    @SuppressLint("SimpleDateFormat")
    fun calculateAge(dob: String?, todayD: String?): String {
        var years = 0
        var months = 0
        var days = 0
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        try {
            val birthDate = sdf.parse(dob!!)
            val todayDate = sdf.parse(todayD!!)

            //create calendar object for birth day
            val birthDay = Calendar.getInstance()
            birthDay.timeInMillis = birthDate!!.time
            //create calendar object for current day
            val todayDay = Calendar.getInstance()
            todayDay.timeInMillis = todayDate!!.time
            //Get difference between years
            years = todayDay[Calendar.YEAR] - birthDay[Calendar.YEAR]
            val currMonth = todayDay[Calendar.MONTH] + 1
            val birthMonth = birthDay[Calendar.MONTH] + 1

            //Get difference between months
            months = currMonth - birthMonth

            //if month difference is in negative then reduce years by one and calculate the number of months.
            if (months < 0) {
                years--
                months = 12 - birthMonth + currMonth
                if (todayDay[Calendar.DATE] < birthDay[Calendar.DATE]) months--
            } else if (months == 0 && todayDay[Calendar.DATE] < birthDay[Calendar.DATE]) {
                years--
                months = 11
            }
            //Calculate the days
            if (todayDay[Calendar.DATE] > birthDay[Calendar.DATE]) days =
                todayDay[Calendar.DATE] - birthDay[Calendar.DATE] else if (todayDay[Calendar.DATE] < birthDay[Calendar.DATE]) {
                val today = todayDay[Calendar.DAY_OF_MONTH]
                todayDay.add(Calendar.MONTH, -1)
                days =
                    todayDay.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay[Calendar.DAY_OF_MONTH] + today
            } else {
                days = 0
                if (months == 12) {
                    years++
                    months = 0
                }
            }
            if (currMonth > birthMonth) {
                if (birthDay[Calendar.DATE] > todayDay[Calendar.DATE]) {
                    months -= 1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return years.toString() + " " + getString(R.string.years) + " | " + months + " " + getString(
            R.string.months
        ) + " | " + days + " " + getString(R.string.days)
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