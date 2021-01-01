package com.example.rentall.ui.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rentall.R
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.di.Injection
import com.example.rentall.viewmodel.MainViewModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_account.*


class EditAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var userEntity: UserEntity? = UserEntity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        viewModel.getUserDetail().observe(this, { user ->
            userEntity = user
            loadData()
        })

        activity_edit_acc_btn_edit.setOnClickListener {
            uploadData()
        }
    }

    private fun loadData() {
        activity_edit_acc_et_fullname.setText(userEntity?.fullname)
        activity_edit_acc_et_phone.setText(userEntity?.phone)
        activity_edit_acc_et_address.setText(userEntity?.address)
    }

    private fun uploadData() {
        userEntity?.fullname = activity_edit_acc_et_fullname.text.toString()
        userEntity?.phone = activity_edit_acc_et_phone.text.toString()
        userEntity?.address = activity_edit_acc_et_address.text.toString()
        viewModel.editAccount(userEntity)
        val intent = Intent(this@EditAccountActivity, UserAccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}

