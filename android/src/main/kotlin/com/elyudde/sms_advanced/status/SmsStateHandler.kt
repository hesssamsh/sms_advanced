package com.elyudde.sms_advanced.status

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.EventChannel

class SmsStateHandler(
    private val context: Context,
    private val eventSink: EventChannel.EventSink?
) {

    private var sentReceiver: SmsSentReceiver? = null
    private var deliveredReceiver: SmsDeliveredReceiver? = null

    fun startListening() {
        val sentFilter = IntentFilter(SmsSentReceiver.ACTION_SENT).apply {
            priority = 1000
        }
        val deliveredFilter = IntentFilter(SmsDeliveredReceiver.ACTION_DELIVERED).apply {
            priority = 1000
        }

        sentReceiver = SmsSentReceiver(eventSink)
        deliveredReceiver = SmsDeliveredReceiver(eventSink)

        // Register with proper RECEIVER_EXPORTED flag for Android 14+
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.registerReceiver(sentReceiver, sentFilter, ContextCompat.RECEIVER_EXPORTED)
            context.registerReceiver(deliveredReceiver, deliveredFilter, ContextCompat.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(sentReceiver, sentFilter)
            context.registerReceiver(deliveredReceiver, deliveredFilter)
        }
    }

    fun stopListening() {
        try {
            sentReceiver?.let { context.unregisterReceiver(it) }
            deliveredReceiver?.let { context.unregisterReceiver(it) }
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
        sentReceiver = null
        deliveredReceiver = null
    }
}

// Sent status receiver
class SmsSentReceiver(private val eventSink: EventChannel.EventSink?) : BroadcastReceiver() {
    companion object {
        const val ACTION_SENT = "com.elyudde.sms_advanced.SMS_SENT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getLongExtra("message_id", -1)
        val result = when (resultCode) {
            android.app.Activity.RESULT_OK -> "SENT"
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> "FAILED_GENERIC"
            SmsManager.RESULT_ERROR_NO_SERVICE -> "FAILED_NO_SERVICE"
            SmsManager.RESULT_ERROR_NULL_PDU -> "FAILED_NULL_PDU"
            SmsManager.RESULT_ERROR_RADIO_OFF -> "FAILED_RADIO_OFF"
            else -> "FAILED_UNKNOWN"
        }
        eventSink?.success(mapOf("id" to messageId, "state" to result))
    }
}

// Delivered status receiver
class SmsDeliveredReceiver(private val eventSink: EventChannel.EventSink?) : BroadcastReceiver() {
    companion object {
        const val ACTION_DELIVERED = "com.elyudde.sms_advanced.SMS_DELIVERED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getLongExtra("message_id", -1)
        val result = when (resultCode) {
            android.app.Activity.RESULT_OK -> "DELIVERED"
            else -> "NOT_DELIVERED"
        }
        eventSink?.success(mapOf("id" to messageId, "state" to result))
    }
}