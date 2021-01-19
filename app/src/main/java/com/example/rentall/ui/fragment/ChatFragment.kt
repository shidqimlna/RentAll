package com.example.rentall.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
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

            viewModel.getUserChatList().observe(this, { products ->
                groupAdapter.setData(products)
            })

            with(fragment_chat_rv_group) {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = groupAdapter
            }
        }
    }
}