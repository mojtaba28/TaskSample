package com.example.sample


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage


class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Rest of your existing onReceive logic
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent?.action) {
            // Handle the incoming SMS here
            // You can extract information from the intent like sender's number and message body
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val pdus: Array<*>? = bundle["pdus"] as Array<*>?
                if (pdus != null) {
                    for (pdu in pdus) {
                        val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                        val sender: String = smsMessage.originatingAddress ?: ""
                        val messageBody: String = smsMessage.messageBody ?: ""

                        // Perform your desired action with sender and messageBody
                        // You may want to show a notification here
                        val notificationManager = PushNotificationManager.getInstance(context!!)
                        notificationManager.showNotification(sender,messageBody)

                    }
                }
            }
        }
    }




}