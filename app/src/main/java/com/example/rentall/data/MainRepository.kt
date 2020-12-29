package com.example.rentall.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.firebase.RemoteDataSource

class MainRepository constructor(private val remoteDataSource: RemoteDataSource) :
    MainRepositoryInterface {

    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(remoteData: RemoteDataSource): MainRepository =
            instance ?: synchronized(this) {
                instance ?: MainRepository(remoteData)
            }
    }

    override fun getProductList(query: String?): LiveData<List<ProductEntity?>> {
        val movieResults = MutableLiveData<List<ProductEntity?>>()
        remoteDataSource.getProductList(query, object : RemoteDataSource.LoadProductsCallback {
            override fun onAllProductsReceived(productResponse: List<ProductEntity?>) {
                movieResults.postValue(productResponse)
            }
        })
        return movieResults
    }

}
