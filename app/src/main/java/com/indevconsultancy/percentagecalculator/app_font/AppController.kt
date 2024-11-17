package com.indevconsultancy.percentagecalculator.app_font

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import com.google.firebase.FirebaseApp
import java.io.File

class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mContext = applicationContext
        FirebaseApp.initializeApp(this)

        createNotificationChannel()
        val directory: File? = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "Percentage Calculator"
                )
            } else {
                applicationContext.getDir("Percentage Calculator", MODE_PRIVATE)
            }
        if (!directory!!.exists()) {
            directory.mkdirs()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val channelDescription = "Channel for my notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mInstance: AppController? = null
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
    }
}