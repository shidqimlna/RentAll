package com.example.rentall.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rentall.data.MainRepository
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private var query: String? = null

    fun setQuery(query: String?) {
        this.query = query
    }

    fun getProductList(): LiveData<List<ProductEntity?>> {
        return mainRepository.getProductList(query)
    }

    fun getUserDetail(): LiveData<UserEntity?> {
        return mainRepository.getUserDetail()
    }

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?) {
        return mainRepository.addProduct(productEntity, filePath)
    }

    fun editProduct(productEntity: ProductEntity?, filePath: Uri?) {
        return mainRepository.editProduct(productEntity, filePath)
    }

    fun deleteProduct(productEntity: ProductEntity?) {
        return mainRepository.deleteProduct(productEntity)
    }

    fun rentProduct(productEntity: ProductEntity?) {
        mainRepository.rentProduct(productEntity)
    }

    fun chatOwner(productEntity: ProductEntity?) {
        mainRepository.chatOwner(productEntity)
    }

}