package com.example.rentall.data


import androidx.lifecycle.LiveData
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity

interface MainRepositoryInterface {

    fun getUserDetail(): LiveData<UserEntity?>
    fun getProductList(query: String?): LiveData<List<ProductEntity?>>

}