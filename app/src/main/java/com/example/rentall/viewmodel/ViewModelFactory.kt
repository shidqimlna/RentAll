package com.example.rentall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rentall.data.MainRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(private val repository: MainRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            return when {
//                isAssignableFrom(ChatViewModel::class.java) -> {
//                    ChatViewModel(repository) as T
//                }
                isAssignableFrom(ProductViewModel::class.java) -> {
                    ProductViewModel(repository) as T
                }
//                isAssignableFrom(FavoriteViewModel::class.java) -> {
//                    FavoriteViewModel(repository) as T
//                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
