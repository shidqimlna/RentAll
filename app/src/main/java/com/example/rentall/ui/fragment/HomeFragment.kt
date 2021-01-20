package com.example.rentall.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rentall.R
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.account.UserAccountActivity
import com.example.rentall.ui.adapter.ProductAdapter
import com.example.rentall.util.Helper.getGreeting
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


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

            val timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            fragment_home_tv_greeting.text = getGreeting(timeOfDay)

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
                layoutManager = GridLayoutManager(context, 2)
                adapter = productAdapter
            }
        }
    }

    private fun searchProduct() {
        val searchQuery = fragment_home_et_search.text.toString()
        viewModel.getProductList(searchQuery).observe(this, { products ->
            productAdapter.setData(products)
        })
    }
}