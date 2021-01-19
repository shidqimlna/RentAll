package com.example.rentall.ui.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rentall.R
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.product.NewProductActivity
import com.example.rentall.ui.activity.splash.LandingActivity
import com.example.rentall.ui.adapter.UserProductAdapter
import com.example.rentall.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_account.*
import kotlinx.android.synthetic.main.activity_user_account.*

class UserAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)

        val userProductAdapter = UserProductAdapter()

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        viewModel.getUserDetail().observe(this, { user ->
            loadData(user)
        })

        viewModel.getUserProductList().observe(this, { products ->
            userProductAdapter.setData(products)
        })

        with(activity_user_acc_rv_product) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = userProductAdapter
        }

        activity_user_acc_btn_signout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@UserAccountActivity, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_user_acc_btn_update.setOnClickListener {
            val intent = Intent(this@UserAccountActivity, EditAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_user_acc_fab_add.setOnClickListener {
            val intent = Intent(this@UserAccountActivity, NewProductActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadData(userEntity: UserEntity?) {
        activity_user_acc_tv_email.text = userEntity?.email
        activity_user_acc_tv_username.text = userEntity?.fullname
        activity_user_acc_tv_address.text = userEntity?.address
        activity_user_acc_tv_phone.text = userEntity?.phone
    }

}