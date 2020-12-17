package com.example.rentall

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseListener: AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        firebaseListener = AuthStateListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val intent = Intent(this@LoginActivity, UserAccountActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        activity_login_btn_login.setOnClickListener {
            val email: String = activity_login_et_email.text.toString()
            val password: String = activity_login_et_password.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
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