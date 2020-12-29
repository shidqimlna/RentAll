package com.example.rentall.util

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult

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
}