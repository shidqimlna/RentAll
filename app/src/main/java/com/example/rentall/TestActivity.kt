package com.example.rentall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser!!.uid
        val reference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                        .child(userId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<String, Any?>?
                    if (map!!["email"] != null) {
                        activity_test_tv_email.text = map["email"].toString()
                        activity_test_tv_fullname.text = map["fullname"].toString()
                        activity_test_tv_address.text = map["address"].toString()
                        activity_test_tv_phone.text = map["phone"].toString()

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_test_btn_signout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this@TestActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_test_btn_update.setOnClickListener {
            val intent = Intent(this@TestActivity, EditAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}