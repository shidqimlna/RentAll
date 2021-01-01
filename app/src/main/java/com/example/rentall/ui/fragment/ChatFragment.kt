package com.example.rentall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentall.R
import com.example.rentall.di.Injection
import com.example.rentall.ui.adapter.GroupAdapter
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            val groupAdapter = GroupAdapter()

            viewModel = ViewModelProvider(
                this,
                Injection.provideViewModelFactory()
            )[MainViewModel::class.java]

//            val listUserChats = ArrayList<ProductEntity?>()
//            val userId = FirebaseAuth.getInstance().currentUser!!.uid
//            val userChatRef: DatabaseReference =
//                FirebaseDatabase.getInstance().reference.child("Users")
//                    .child(userId).child("Chats")
//            userChatRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    for (dataSnapshot1 in dataSnapshot.children) {
//                        val userRent: ProductEntity? =
//                            dataSnapshot1.getValue(ProductEntity::class.java)
//                        val productRef: Query =
//                            FirebaseDatabase.getInstance().reference.child("Products")
//                                .orderByChild("id")
//                                .equalTo(userRent?.id)
//                        productRef.addValueEventListener(object : ValueEventListener {
//                            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    for (dataSnapshot2 in dataSnapshot.children) {
//                                        val productEntity: ProductEntity? =
//                                            dataSnapshot2.getValue(ProductEntity::class.java)
//                                        productEntity?.time = userRent?.time
//                                        listUserChats.add(productEntity)
//                                    }
//                                    chatAdapter.groupAdapter(listUserChats)
//                                    chatAdapter.notifyDataSetChanged()
//                                } else {
//                                    val userRentIdRef = userChatRef.child(userRent?.id!!)
//                                    userRentIdRef.removeValue()
//                                }
//                            }
//
//                            override fun onCancelled(databaseError: DatabaseError) {}
//                        })
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                }
//            })

            viewModel.getUserChatList().observe(this, { products ->
                groupAdapter.setData(products)
            })

            with(fragment_chat_rv_group) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = groupAdapter
            }
        }
    }
}