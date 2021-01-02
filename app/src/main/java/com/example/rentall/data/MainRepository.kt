package com.example.rentall.data

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.data.firebase.RemoteDataSource
import com.example.rentall.data.firebase.RemoteDataSource.*

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

    override fun registerUser(userEntity: UserEntity?, password: String, activity: Activity) {
        remoteDataSource.registerUser(userEntity, password, activity)
    }

    override fun loginUser(email: String?, password: String?, activity: Activity) {
        remoteDataSource.loginUser(email, password, activity)
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

    override fun editAccount(userEntity: UserEntity?) {
        remoteDataSource.editAccount(userEntity)
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

    override fun getUserProductList(): LiveData<List<ProductEntity?>> {
        val productResult = MutableLiveData<List<ProductEntity?>>()
        remoteDataSource.getUserProductList(object : LoadProductsCallback {
            override fun onAllProductsReceived(productResponse: List<ProductEntity?>) {
                productResult.postValue(productResponse)
            }
        })
        return productResult
    }

    override fun getUserChatList(): LiveData<List<ProductEntity?>> {
        val productResult = MutableLiveData<List<ProductEntity?>>()
        remoteDataSource.getUserChatList(object : LoadProductsCallback {
            override fun onAllProductsReceived(productResponse: List<ProductEntity?>) {
                productResult.postValue(productResponse)
            }
        })
        return productResult
    }

    override fun getUserRentingHistoryList(): LiveData<List<ProductEntity?>> {
        val productResult = MutableLiveData<List<ProductEntity?>>()
        remoteDataSource.getUserRentingHistoryList(object : LoadProductsCallback {
            override fun onAllProductsReceived(productResponse: List<ProductEntity?>) {
                productResult.postValue(productResponse)
            }
        })
        return productResult
    }

    override fun addProduct(productEntity: ProductEntity?, filePath: Uri?) {
        remoteDataSource.addProduct(productEntity, filePath)
    }

    override fun editProduct(productEntity: ProductEntity?, filePath: Uri?) {
        remoteDataSource.editProduct(productEntity, filePath)
    }

    override fun deleteProduct(productEntity: ProductEntity?) {
        remoteDataSource.deleteProduct(productEntity)
    }

    override fun sendMessage(chatEntity: ChatEntity, productEntity: ProductEntity?) {
        remoteDataSource.sendMessage(chatEntity, productEntity)
    }

    override fun getMessages(productEntity: ProductEntity?): LiveData<List<ChatEntity?>> {
        val chatResults = MutableLiveData<List<ChatEntity?>>()
        remoteDataSource.getMessages(productEntity, object : LoadChatMessagesCallback {
            override fun onChatMessagesReceived(chatResponse: List<ChatEntity?>) {
                chatResults.postValue(chatResponse)
            }
        })
        return chatResults
    }

    override fun rentProduct(productEntity: ProductEntity?) {
        remoteDataSource.rentProduct(productEntity)
    }

    override fun chatOwner(productEntity: ProductEntity?) {
        remoteDataSource.chatOwner(productEntity)
    }

}
