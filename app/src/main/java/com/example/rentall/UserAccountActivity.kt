package com.example.rentall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_account.*


class UserAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var listUserProduct = ArrayList<ProductEntity?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account)

        val userProductAdapter = UserProductAdapter()

        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser!!.uid
        val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                        .child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

        val userProductRef = userRef.child("Products")
        userProductRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val productEntity: ProductEntity? =
                        dataSnapshot1.getValue(ProductEntity::class.java)
                    val productRef: Query =
                        FirebaseDatabase.getInstance().reference.child("Products")
                            .orderByChild("id")
                            .equalTo(productEntity?.id)
                    productRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (dataSnapshot2 in dataSnapshot.children) {
                                    val userProduct: ProductEntity? =
                                        dataSnapshot2.getValue(ProductEntity::class.java)
                                    listUserProduct.add(userProduct)
                                }
                                userProductAdapter.userProductAdapter(listUserProduct)
                                userProductAdapter.notifyDataSetChanged()
                            } else {
                                val userProductIdRef = userProductRef.child(productEntity?.id!!)
                                userProductIdRef.removeValue()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        with(activity_user_acc_rv_product) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userProductAdapter
        }

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