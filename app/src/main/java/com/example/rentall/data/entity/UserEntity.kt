package com.example.rentall.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(
    val id: String? = null,
    var fullname: String? = null,
    var address: String? = null,
    var email: String? = null,
    var phone: String? = null
) : Parcelable