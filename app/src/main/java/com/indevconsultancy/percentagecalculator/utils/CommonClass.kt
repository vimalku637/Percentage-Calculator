package com.indevconsultancy.percentagecalculator.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

    fun showCustomToast(applicationContext: Context, message: String) {
        // Create a parent LinearLayout for the custom Toast
        val toastLayout = LinearLayout(applicationContext).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 24, 16, 24)
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(Color.parseColor("#FF018786")) // Set background color
            }
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        // Create an ImageView for the icon
        val icon = ImageView(applicationContext).apply {
            setImageResource(android.R.drawable.ic_dialog_info) // Replace with your drawable if needed
            setColorFilter(Color.WHITE) // Set icon color
            layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                setMargins(24, 0, 24, 0)
            }
        }

        // Create a TextView for the message
        val textView = TextView(applicationContext).apply {
            text = message
            setTextColor(Color.WHITE)
            textSize = 16f
        }

        // Add the icon and message to the layout
        toastLayout.addView(icon)
        toastLayout.addView(textView)

        // Create and show the Toast
        val toast = Toast(applicationContext).apply {
            duration = Toast.LENGTH_LONG
            view = toastLayout
            setGravity(Gravity.CENTER_HORIZONTAL or Gravity.FILL_HORIZONTAL, 0, 100)
        }
        toast.show()
    }
}