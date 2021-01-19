package com.example.rentall.ui.activity.account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rentall.R
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.main.MainActivity
import com.example.rentall.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var authStateListener: AuthStateListener
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        authStateListener = AuthStateListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        activity_register_btn_register.setOnClickListener {
            uploadData()
        }
    }

    private fun uploadData() {
        val userEntity: UserEntity? = UserEntity()
        val password = activity_register_et_password.text.toString()
        userEntity?.email = activity_register_et_email.text.toString()
        userEntity?.fullname = activity_register_et_username.text.toString()
        userEntity?.phone = activity_register_et_phone.text.toString()
        userEntity?.address = activity_register_et_address.text.toString()
        viewModel.registerUser(userEntity, password, this)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }
}