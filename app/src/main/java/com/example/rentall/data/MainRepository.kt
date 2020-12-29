package com.example.rentall.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.data.firebase.RemoteDataSource
import com.example.rentall.data.firebase.RemoteDataSource.LoadProductsCallback
import com.example.rentall.data.firebase.RemoteDataSource.LoadUserDetailCallback

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

    override fun getUserDetail(): LiveData<UserEntity?> {
        val userResult = MutableLiveData<UserEntity?>()
        remoteDataSource.getUserDetail(object : LoadUserDetailCallback {
            override fun onUserDetailReceived(userResponse: UserEntity?) {
                userResult.postValue(userResponse)
            }
        })
        return userResult
    }

    override fun getProductList(query: String?): LiveData<List<ProductEntity?>> {
        val productResult = MutableLiveData<List<ProductEntity?>>()
        remoteDataSource.getProductList(query, object : LoadProductsCallback {
            override fun onAllProductsReceived(productResponse: List<ProductEntity?>) {
                productResult.postValue(productResponse)
            }
        })
        return productResult
    }

    override fun addProduct(productEntity: ProductEntity?, filePath: Uri?, context: Context) {
        remoteDataSource.addProduct(productEntity, filePath, context)
    }

}
