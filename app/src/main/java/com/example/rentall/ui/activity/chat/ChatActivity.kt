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
        })

        viewModel.getMessages(productEntity).observe(this, { chats ->
            chatAdapter.setChat(chats)
            activity_chat_rv.scrollToPosition(chatAdapter.itemCount - 1)
        })

        with(activity_chat_rv) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        activity_chat_btn_send.setOnClickListener {
            sendMessage()
            activity_chat_et_text.setText("")
        }

    }

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
        }
    }

}