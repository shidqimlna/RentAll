package com.example.rentall.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity

interface MainRepositoryInterface {
    fun getUserDetail(): LiveData<UserEntity?>
    fun getProductList(query: String?): LiveData<List<ProductEntity?>>
    fun addProduct(productEntity: ProductEntity?, filePath: Uri?)
    fun rentProduct(productEntity: ProductEntity?)
    fun chatOwner(productEntity: ProductEntity?)
}