package com.indevconsultancy.percentagecalculator.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.indevconsultancy.percentagecalculator.R
import com.indevconsultancy.percentagecalculator.receivers.NotificationReceiver

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate() {
        super.onCreate()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            handleDataMessage(remoteMessage.data)
        }
        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
            handleNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!.body
            )
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"]
        val body = data["body"]
        val extraData = data["extra_data"]
        logAnalyticsEvent(title, body, extraData)
        sendBroadcastIntent(title, body, extraData)
        showNotification(title, body, extraData)
    }

    private fun logAnalyticsEvent(title: String?, body: String?, extraData: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, title)
        bundle.putString(FirebaseAnalytics.Param.CONTENT, body)
        bundle.putString("extra_data", extraData)
        mFirebaseAnalytics!!.logEvent("notification_received", bundle)
    }

    private fun handleNotification(title: String?, body: String?) {
        showNotification(title, body, null)
    }

    private fun sendBroadcastIntent(title: String?, body: String?, extraData: String?) {
        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("title", title)
        broadcastIntent.putExtra("body", body)
        broadcastIntent.putExtra("extra_data", extraData)
        sendBroadcast(broadcastIntent)
    }

    @SuppressLint("NotificationTrampoline", "LaunchActivityFromNotification")
    private fun showNotification(title: String?, body: String?, extraData: String?) {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("body", body)
        intent.putExtra("extra_data", extraData)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        //Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationToServer: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "my_channel_id"
    }
}