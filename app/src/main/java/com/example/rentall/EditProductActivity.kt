package com.example.rentall

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_product.*
import kotlinx.android.synthetic.main.activity_new_product.*
import java.io.IOException


class EditProductActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 22
    private var filePath: Uri? = null
    private lateinit var productImageRef: StorageReference
    private lateinit var productRef: DatabaseReference

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""
        productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)
        productImageRef =
            FirebaseStorage.getInstance().reference.child("images/products/$productId")

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val productEntity: ProductEntity? =
                        dataSnapshot.getValue(ProductEntity::class.java)
                    activity_edit_product_et_name.setText(productEntity?.name)
                    activity_edit_product_et_price.setText(productEntity?.price)
                    activity_edit_product_et_desciption.setText(productEntity?.desc)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_edit_product_btn_addimg.setOnClickListener {
            selectImage()
        }

        activity_edit_product_btn_submit.setOnClickListener {
            uploadData()
            uploadImage()
            val intent = Intent(this@EditProductActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_edit_product_btn_delete.setOnClickListener{
            productRef.removeValue()
            productImageRef.delete()
                    val move = Intent(this@EditProductActivity, UserAccountActivity::class.java)
                    startActivity(move)
        }
    }

    private fun uploadData() {
        val productName: String = activity_edit_product_et_name.text.toString()
        val price: String = activity_edit_product_et_price.text.toString()
        val description: String = activity_edit_product_et_desciption.text.toString()
        val productInfo: MutableMap<String, Any> = java.util.HashMap()
        productInfo["name"] = productName
        productInfo["price"] = price
        productInfo["desc"] = description
        productRef.updateChildren(productInfo)
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                activity_edit_product_iv_productimg.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            productImageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    try {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@EditProductActivity,
                            "Data Uploaded!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .addOnFailureListener { e ->
                    try {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@EditProductActivity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .addOnProgressListener { taskSnapshot ->
                    try {
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}