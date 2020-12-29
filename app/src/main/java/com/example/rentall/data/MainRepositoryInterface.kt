package com.example.rentall.data


import androidx.lifecycle.LiveData
import com.example.rentall.data.entity.ProductEntity

interface MainRepositoryInterface {

    fun getProductList(query: String?): LiveData<List<ProductEntity?>>

}