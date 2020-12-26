package com.example.rentall

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductEntity(
    val id: String? = null,
    val name: String? = null,
    val price: String? = null,
    val desc: String? = null,
    val owner: String? = null,
    var time: String? = null
) : Parcelable