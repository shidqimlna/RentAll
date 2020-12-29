package com.example.rentall.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rentall.data.MainRepository
import com.example.rentall.data.entity.ProductEntity

class ProductViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private var query: String? = null

    fun setQuery(query: String?) {
        this.query = query
    }

    fun getProductList(): LiveData<List<ProductEntity?>> {
        return mainRepository.getProductList(query)
    }

}