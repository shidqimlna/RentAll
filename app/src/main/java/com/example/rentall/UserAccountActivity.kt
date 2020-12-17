package com.example.rentall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_account.*

class UserAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)

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
                        activity_user_acc_tv_email.text = map["email"].toString()
                        activity_user_acc_tv_fullname.text = map["fullname"].toString()
                        activity_user_acc_tv_address.text = map["address"].toString()
                        activity_user_acc_tv_phone.text = map["phone"].toString()

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_user_acc_btn_signout.setOnClickListener {
            firebaseAuth.signOut()
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

}