package com.indevconsultancy.percentagecalculator.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar

object CommonClass {
    @JvmStatic
    val currentDateTime: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val dateFormat =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val c = Calendar.getInstance()
            return dateFormat.format(c.time)
        }
}