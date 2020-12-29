package com.example.rentall.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(
    val id: String? = null,
    val fullname: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phone: String? = null
) : Parcelable