package com.example.rentall

import android.os.Bundle
import android.text.TextUtils
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var chatRef: DatabaseReference

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val productId = intent.extras?.getString(DetailProductActivity.EXTRA_PRODUCT) ?: ""
        chatRef = FirebaseDatabase.getInstance().reference.child("Chats").child(productId)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<String, Any?>?
                    if (map!!["fullname"] != null) {
                        userName = map["fullname"].toString()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val productEntity = dataSnapshot.getValue(ProductEntity::class.java)
                    activity_chat_tv_productname.text = productEntity?.name
                    activity_chat_tv_owner.text = productEntity?.owner
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_chat_btn_send.setOnClickListener {
            saveMessageInfoToDatabase()
            activity_chat_et_text.setText("")
            activity_chat_sv_chat.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }


    override fun onStart() {
        super.onStart()
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, @Nullable s: String?) {
                if (dataSnapshot.exists()) {
                    displayMessages(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, @Nullable s: String?) {
                if (dataSnapshot.exists()) {
                    displayMessages(dataSnapshot)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, @Nullable s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun displayMessages(dataSnapshot: DataSnapshot) {
//        if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
//                for (dataSnapshot2 in dataSnapshot.children) {
//                    val map = dataSnapshot.value as Map<String, Any?>?
//                    if (map!!["message"] != null) {
//                        val chatName = map["name"].toString()
//                        val chatMessage = map["message"].toString()
//                        val chatTime = map["time"].toString()
//                        activity_chat_tv_chat.append("$chatName :\n$chatMessage\n$chatTime\n\n")
//                        activity_chat_sv_chat.fullScroll(ScrollView.FOCUS_DOWN)
//                    }
//        }
        val iterator: Iterator<*> = dataSnapshot.children.iterator()
        while (iterator.hasNext()) {
            val chatMessage = (iterator.next() as DataSnapshot).value as String?
            val chatName = (iterator.next() as DataSnapshot).value as String?
            val chatTime = (iterator.next() as DataSnapshot).value as String?
            activity_chat_tv_chat.append("$chatName :\n$chatMessage\n$chatTime\n\n")
            activity_chat_sv_chat.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun saveMessageInfoToDatabase() {
        val message = activity_chat_et_text.text.toString()
        val messageKEY = chatRef.push().key
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show()
        } else {
            val chatMessageKeyRef = chatRef.child(messageKEY!!)
            val messageInfoMap: HashMap<String, Any> = HashMap()
            messageInfoMap["name"] = userName
            messageInfoMap["message"] = message
            messageInfoMap["time"] = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(
                Calendar.getInstance().time
            )
            chatMessageKeyRef.updateChildren(messageInfoMap)
        }
    }

}