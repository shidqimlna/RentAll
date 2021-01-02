package com.example.rentall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentall.R
import com.example.rentall.di.Injection
import com.example.rentall.ui.adapter.RentHistoryAdapter
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val rentHistoryAdapter = RentHistoryAdapter()

            viewModel = ViewModelProvider(
                this,
                Injection.provideViewModelFactory()
            )[MainViewModel::class.java]

            viewModel.getUserRentingHistoryList().observe(this, { products ->
                rentHistoryAdapter.setData(products)
            })

            with(fragment_history_rv_product) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = rentHistoryAdapter
            }
        }
    }
}