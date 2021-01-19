package com.example.rentall.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RentEntity(
    var id: String? = null,
    var time: String? = null,
    var productEntity: ProductEntity? = null
) : Parcelable