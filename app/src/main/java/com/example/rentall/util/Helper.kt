package com.example.rentall.util

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import java.text.DecimalFormat

object Helper {

    const val PICK_IMAGE_REQUEST = 22

    fun selectImage(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            activity,
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST,
            null
        )
    }

    fun getGreeting(timeOfDay: Int): String {
        var greeting = String()
        when (timeOfDay) {
            in 0..11 -> greeting = "Good Morning,"
            in 12..15 -> greeting = "Good Afternoon,"
            in 16..20 -> greeting = "Good Evening,"
            in 21..23 -> greeting = "Good Night,"
        }
        return greeting
    }

    fun currencyFormatter(num: Int?): String = "Rp" + DecimalFormat("###,###,###").format(num)

}