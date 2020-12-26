package com.example.rentall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    private var listUserRents = ArrayList<ProductEntity?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val rentHistoryAdapter = RentHistoryAdapter()

            val firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser!!.uid
            val userRentRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(userId).child("Rents")
            userRentRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val userRent: ProductEntity? =
                            dataSnapshot1.getValue(ProductEntity::class.java)
                        val productRef: Query =
                            FirebaseDatabase.getInstance().reference.child("Products")
                                .orderByChild("id")
                                .equalTo(userRent?.id)
                        productRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (dataSnapshot2 in dataSnapshot.children) {
                                        val productEntity: ProductEntity? =
                                            dataSnapshot2.getValue(ProductEntity::class.java)
                                        productEntity?.time = userRent?.time
                                        listUserRents.add(productEntity)
                                    }
                                    rentHistoryAdapter.rentHistoryAdapter(listUserRents)
                                    rentHistoryAdapter.notifyDataSetChanged()
                                } else {
                                    val userRentIdRef = userRentRef.child(userRent?.id!!)
                                    userRentIdRef.removeValue()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            with(fragment_history_rv_product) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = rentHistoryAdapter
            }
        }
    }
}