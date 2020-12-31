package com.example.rentall.ui.activity.chat

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentall.R
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.product.DetailProductActivity
import com.example.rentall.ui.adapter.ChatAdapter
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var username: String

    //    private lateinit var chatRef: DatabaseReference
    private lateinit var viewModel: MainViewModel
    private var productEntity: ProductEntity? = ProductEntity()
    private lateinit var chatAdapter: ChatAdapter

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        productEntity = intent.extras?.getParcelable(DetailProductActivity.EXTRA_PRODUCT)

        activity_chat_tv_productname.text = productEntity?.name
        activity_chat_tv_owner.text = productEntity?.owner

        chatAdapter = ChatAdapter()

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        viewModel.getUserDetail().observe(this, { user ->
            username = user?.fullname!!
            chatAdapter.setUser(username)
            viewModel.getMessages(productEntity).observe(this, { chats ->
                chatAdapter.setChat(chats)
                activity_chat_rv.scrollToPosition(chatAdapter.itemCount - 1)
            })
        })

//        chatRef = FirebaseDatabase.getInstance().reference.child("Chats").child(productId)


//        viewModel = ViewModelProvider(
//            this,
//            Injection.provideViewModelFactory(this)
//        )[ChatViewModel::class.java]

//        val extras = intent.extras
//        if (extras != null) {
//            val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""
//            viewModel.setProductId(productId)
//            viewModel.getMovieDetail().observe(this, { movie ->
//                activity_detail_movie_progressBar_layout.visibility = View.GONE
//                loadData(movie)
//            })
//        }

//        val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""


//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//        val userRef: DatabaseReference =
//            FirebaseDatabase.getInstance().reference.child("Users")
//                .child(userId)
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
//                    val map = dataSnapshot.value as Map<String, Any?>?
//                    if (map!!["fullname"] != null) {
//                        username = map["fullname"].toString()
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })

//        val productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)
//        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
//                    val productEntity = dataSnapshot.getValue(ProductEntity::class.java)
//                    activity_chat_tv_productname.text = productEntity?.name
//                    activity_chat_tv_owner.text = productEntity?.owner
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })


        with(activity_chat_rv) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        activity_chat_btn_send.setOnClickListener {
            sendMessage()
            activity_chat_et_text.setText("")
//            activity_chat_rv.scrollToPosition(chatAdapter.itemCount - 1)
        }


    }

//    override fun onObserve(event: Int, eventMessage: ArrayList<ChatEntity?>) {
//        chatAdapter.chatAdapter(eventMessage)
//        activity_chat_rv.scrollToPosition(eventMessage.size - 1)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        viewModel.removeObserver(this)
//        viewModel.onDestory()
//    }

//    override fun onStart() {
//        super.onStart()
//        chatRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, @Nullable s: String?) {
//                if (dataSnapshot.exists()) {
//                    displayMessages(dataSnapshot)
//                }
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, @Nullable s: String?) {
//                if (dataSnapshot.exists()) {
//                    displayMessages(dataSnapshot)
//                }
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
//            override fun onChildMoved(dataSnapshot: DataSnapshot, @Nullable s: String?) {}
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })
//    }

//    private fun displayMessages(dataSnapshot: DataSnapshot) {
////        if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
////                for (dataSnapshot2 in dataSnapshot.children) {
////                    val map = dataSnapshot.value as Map<String, Any?>?
////                    if (map!!["message"] != null) {
////                        val chatName = map["name"].toString()
////                        val chatMessage = map["message"].toString()
////                        val chatTime = map["time"].toString()
////                        activity_chat_tv_chat.append("$chatName :\n$chatMessage\n$chatTime\n\n")
////                        activity_chat_sv_chat.fullScroll(ScrollView.FOCUS_DOWN)
////                    }
////        }
//        val iterator: Iterator<*> = dataSnapshot.children.iterator()
//        while (iterator.hasNext()) {
//            val chatMessage = (iterator.next() as DataSnapshot).value as String?
//            val chatName = (iterator.next() as DataSnapshot).value as String?
//            val chatTime = (iterator.next() as DataSnapshot).value as String?
//            activity_chat_tv_chat.append("$chatName :\n$chatMessage\n$chatTime\n\n")
//            activity_chat_sv_chat.fullScroll(ScrollView.FOCUS_DOWN)
//        }
//    }

    private fun sendMessage() {
        if (TextUtils.isEmpty(activity_chat_et_text.text.toString())) {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show()
        } else {
            val chatEntity = ChatEntity()
            chatEntity.message = activity_chat_et_text.text.toString()
            chatEntity.name = username
            chatEntity.time = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(
                Calendar.getInstance().time
            )
            viewModel.sendMessage(chatEntity, productEntity)

//            val chatMessageKeyRef = chatRef.child(messageKEY!!)
//            val messageInfoMap: HashMap<String, Any> = HashMap()
//            messageInfoMap["name"] = username
//            messageInfoMap["message"] = message
//            messageInfoMap["time"] = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(
//                Calendar.getInstance().time
//            )
//            chatMessageKeyRef.updateChildren(messageInfoMap)
        }
    }

}