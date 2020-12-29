package com.example.rentall.di

import androidx.lifecycle.ViewModelProvider
import com.example.rentall.data.MainRepository
import com.example.rentall.data.firebase.RemoteDataSource
import com.example.rentall.viewmodel.ViewModelFactory

object Injection {
    private fun provideRepository(): MainRepository {
        return MainRepository.getInstance(RemoteDataSource.getInstance())
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(provideRepository())
    }
}
