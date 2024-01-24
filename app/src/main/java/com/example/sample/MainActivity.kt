package com.example.sample

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var phoneEdt: EditText
    private lateinit var messageEdt: EditText
    private lateinit var sendMsgBtn: Button
    private lateinit var smsReceiver: SmsReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        sendBtnClicked()
        registerSmsReceiver()


    }


    private fun init() {
        smsReceiver = SmsReceiver()
        phoneEdt = findViewById(R.id.phoneEdt)
        messageEdt = findViewById(R.id.messageEdt)
        sendMsgBtn = findViewById(R.id.sendBtn)
    }


    private fun sendBtnClicked() {
        sendMsgBtn.setOnClickListener {
            // Check and request SEND_SMS permission if not granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Log the permission request attempt
                Log.d("Permission", "Requesting SEND_SMS permission")

                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    Const.REQUEST_SMS_PERMISSION
                )
            } else {
                // Permission is already granted, proceed to send message
                sendMessage()
            }
        }
    }



    private fun sendMessage() {
        try {
            val phoneNumber = phoneEdt.text.toString()
            val message = messageEdt.text.toString()

            if (phoneNumber.isBlank() || message.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.please_enter_valid_phone_number_and_mesage),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            // Initialize SmsManager
            val smsManager = if (Build.VERSION.SDK_INT >= 23) {
                getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            // Send text message
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)

            // Display success message
            Toast.makeText(applicationContext, getString(R.string.message_sent), Toast.LENGTH_LONG)
                .show()

        } catch (e: SecurityException) {
            // Log the security exception
            Log.e("Permission", "SecurityException: ${e.message}")
            Toast.makeText(
                applicationContext,
                getString(R.string.permission_denied),
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            // Log other exceptions
            e.printStackTrace()
            Log.e("Error", "An error occurred: ${e.message}")
            Toast.makeText(applicationContext, getString(R.string.error_occured), Toast.LENGTH_LONG).show()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Const.REQUEST_SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can now attempt to send SMS again
                    Log.d("Permission", "SEND_SMS permission granted")
                    sendMessage()
                } else {
                    // Permission denied, show a message to the user
                    Log.d("Permission", "SEND_SMS permission denied")
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            // Handle other permission requests if needed
        }
    }

    private fun registerSmsReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Request RECEIVE_SMS permission dynamically
            requestReceiveSmsPermission()
        } else {
            // For devices with SDK < 23, permission is granted in the manifest
            registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        }
    }

    private fun requestReceiveSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                Const.REQUEST_SMS_PERMISSION
            )
        } else {
            // Permission is already granted, register the receiver
            registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(smsReceiver)
    }
}