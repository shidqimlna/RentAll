package com.example.rentall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_account.*


class EditAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser!!.uid
        val reference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                        .child(userId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<String, Any?>?
                    if (map!!["fullname"] != null) {
                        activity_edit_acc_et_fullname.setText(map["fullname"].toString())
                        activity_edit_acc_et_phone.setText(map["phone"].toString())
                        activity_edit_acc_et_address.setText(map["address"].toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_edit_acc_btn_edit.setOnClickListener {
            val fullname: String = activity_edit_acc_et_fullname.text.toString()
            val phone: String = activity_edit_acc_et_phone.text.toString()
            val address: String = activity_edit_acc_et_address.text.toString()

            val userInfo: MutableMap<String, Any> = HashMap()
            userInfo["fullname"] = fullname
            userInfo["phone"] = phone
            userInfo["address"] = address
            reference.updateChildren(userInfo)

            val intent = Intent(this@EditAccountActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

