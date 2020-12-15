package com.example.rentall

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        firebaseListener = AuthStateListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val intent = Intent(this@RegisterActivity, TestActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

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
                        }
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseListener)
    }
}