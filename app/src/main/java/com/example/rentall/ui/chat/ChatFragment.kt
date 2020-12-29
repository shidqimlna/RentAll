package com.example.rentall.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private var listUserChats = ArrayList<ProductEntity?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val chatAdapter = ChatAdapter()
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userChatRef: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(userId).child("Chats")
            userChatRef.addValueEventListener(object : ValueEventListener {
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
                                        listUserChats.add(productEntity)
                                    }
                                    chatAdapter.chatAdapter(listUserChats)
                                    chatAdapter.notifyDataSetChanged()
                                } else {
                                    val userRentIdRef = userChatRef.child(userRent?.id!!)
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

            with(fragment_chat_rv_group) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = chatAdapter
            }
        }
    }
}