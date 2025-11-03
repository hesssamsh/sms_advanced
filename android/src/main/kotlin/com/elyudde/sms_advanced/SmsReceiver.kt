package com.elyudde.sms_advanced

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SENT = "SMS_SENT"
        const val ACTION_DELIVERED = "SMS_DELIVERED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This receiver is not used directly anymore — kept for backward compatibility
        // Status handling is now done in SmsStateHandler
    }

    // Helper to register sent receiver with proper flags
    fun registerSentReceiver(
        context: Context,
        receiver: BroadcastReceiver,
        filter: android.content.IntentFilter
    ) {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(receiver, filter)
        }
    }

    // Helper to register delivered receiver with proper flags
    fun registerDeliveredReceiver(
        context: Context,
        receiver: BroadcastReceiver,
        filter: android.content.IntentFilter
    ) {
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(receiver, filter)
        }
    }
}