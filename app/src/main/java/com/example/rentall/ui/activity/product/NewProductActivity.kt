package com.example.rentall.ui.activity.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.di.Injection
import com.example.rentall.ui.activity.account.UserAccountActivity
import com.example.rentall.util.Helper.PICK_IMAGE_REQUEST
import com.example.rentall.util.Helper.selectImage
import com.example.rentall.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_new_product.*
import java.io.IOException

class NewProductActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        activity_new_product_btn_addimg.setOnClickListener {
            selectImage(this)
        }

        activity_new_product_btn_submit.setOnClickListener {
            uploadData()
        }
    }

    private fun uploadData() {
        if (filePath != null) {
            val productEntity: ProductEntity? = ProductEntity()
            productEntity?.name = activity_new_product_et_name.text.toString()
            productEntity?.price = activity_new_product_et_price.text.toString().toInt()
            productEntity?.desc = activity_new_product_et_desciption.text.toString()
            viewModel.getUserDetail().observe(this, { user ->
                productEntity?.owner = user?.fullname!!
                viewModel.addProduct(productEntity, filePath)
                val intent = Intent(this, UserAccountActivity::class.java)
                startActivity(intent)
                finish()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                activity_new_product_iv_productimg.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}