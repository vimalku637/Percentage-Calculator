package com.indevconsultancy.percentagecalculator.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.indevconsultancy.percentagecalculator.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras != null) {
            val title = intent.getStringExtra("title")
            val body = intent.getStringExtra("body")
            val extraData = intent.getStringExtra("extra_data")
            Log.d("NotificationReceiver", "Received data: $extraData")

            // Start the target activity
            val activityIntent = Intent(context, MainActivity::class.java)
            activityIntent.putExtra("title", title)
            activityIntent.putExtra("body", body)
            activityIntent.putExtra("extra_data", extraData)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(activityIntent)
        }
    }
}