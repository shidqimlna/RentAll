package com.example.rentall.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentall.R
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.account.UserAccountActivity
import com.example.rentall.ui.adapter.ProductAdapter
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            productAdapter = ProductAdapter()

            viewModel = ViewModelProvider(
                this,
                Injection.provideViewModelFactory()
            )[MainViewModel::class.java]

            viewModel.getUserDetail().observe(this, { user ->
                fragment_home_tv_username.text = user?.fullname
            })

            searchProduct()

            fragment_home_btn_search.setOnClickListener {
                searchProduct()
            }

            fragment_home_btn_profile.setOnClickListener {
                val intent = Intent(context, UserAccountActivity::class.java)
                startActivity(intent)
            }

            with(fragment_home_rv) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = productAdapter
            }
        }
    }

    private fun searchProduct() {
        val searchQuery = fragment_home_et_search.text.toString()
        viewModel.setQuery(searchQuery)
        viewModel.getProductList().observe(this, { products ->
            productAdapter.setData(products)
        })
    }
}