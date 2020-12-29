package com.example.rentall.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rentall.R
import com.example.rentall.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        activity_register_btn_register.setOnClickListener {
            val email: String = activity_register_et_email.text.toString()
            val password: String = activity_register_et_password.text.toString()
            val fullname: String = activity_register_et_fullname.text.toString()
            val phone: String = activity_register_et_phone.text.toString()
            val address: String = activity_register_et_address.text.toString()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (!task.isSuccessful || fullname.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "sign up error",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val userId = firebaseAuth.currentUser!!.uid
                        val reference: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("Users")
                                .child(userId)
                        val userInfo: MutableMap<String, Any> = HashMap()
                        userInfo["email"] = email
                        userInfo["fullname"] = fullname
                        userInfo["phone"] = phone
                        userInfo["address"] = address
                        reference.updateChildren(userInfo)

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }
}