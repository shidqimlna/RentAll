package com.example.rentall.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductEntity(
    var id: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var desc: String? = null,
    var owner: String? = null
) : Parcelable