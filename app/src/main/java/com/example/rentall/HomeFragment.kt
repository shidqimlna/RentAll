package com.example.rentall

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            productAdapter = ProductAdapter()

            val firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser!!.uid
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        val map = dataSnapshot.value as Map<String, Any?>?
                        if (map!!["fullname"] != null) {
                            fragment_home_tv_username.text = map["fullname"].toString()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

            searchProduct()

            fragment_home_btn_search.setOnClickListener {
                searchProduct()
            }

            fragment_home_btn_profile.setOnClickListener {
                val intent = Intent(context, UserAccountActivity::class.java)
                startActivity(intent)
            }

            with(fragment_home_rv) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = productAdapter
            }
        }
    }

    private fun searchProduct() {

        val listProduct = ArrayList<ProductEntity?>()
        val searchQuery = fragment_home_et_search.text.toString()

        val productQuery =
            FirebaseDatabase.getInstance().reference.child("Products")
                .orderByChild("name")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")

        productQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val userProduct: ProductEntity? =
                        dataSnapshot1.getValue(ProductEntity::class.java)
                    listProduct.add(userProduct)
                }
                productAdapter.productAdapter(listProduct)
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

}