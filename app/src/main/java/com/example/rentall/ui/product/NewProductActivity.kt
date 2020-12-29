package com.example.rentall.ui.product

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rentall.R
import com.example.rentall.ui.account.UserAccountActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_new_product.*
import java.io.IOException
import java.util.*


class NewProductActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 22
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productRef: DatabaseReference
    private lateinit var userProductRef: DatabaseReference
    private lateinit var userChatRef: DatabaseReference
    private lateinit var productImageRef: StorageReference
    private lateinit var username: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<String, Any?>?
                    if (map!!["fullname"] != null) {
                        username = map["fullname"].toString()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val productId = FirebaseDatabase.getInstance().reference.child("Products").push().key
        productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId!!)
        userProductRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Products")
                .child(productId)
        userChatRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
                .child(productId)
        productImageRef =
            FirebaseStorage.getInstance().reference.child("images/products/$productId")

        activity_new_product_btn_addimg.setOnClickListener {
            selectImage()
        }

        activity_new_product_btn_submit.setOnClickListener {
            uploadData(productId)
            uploadImage()

            val intent = Intent(this@NewProductActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadData(
        productId: String
    ) {
        val productName: String = activity_new_product_et_name.text.toString()
        val price: String = activity_new_product_et_price.text.toString()
        val description: String = activity_new_product_et_desciption.text.toString()

        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productId
        productInfo["name"] = productName
        productInfo["price"] = price
        productInfo["desc"] = description
        productInfo["owner"] = username
        productInfo["ownerId"] = userId
        productRef.updateChildren(productInfo)

        val productInfo2: MutableMap<String, Any> = HashMap()
        productInfo2["id"] = productId
        userProductRef.updateChildren(productInfo2)

        val productInfo3: MutableMap<String, Any> = HashMap()
        productInfo3["id"] = productId
        userChatRef.updateChildren(productInfo3)
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
                activity_new_product_iv_productimg.setImageBitmap(bitmap)
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
                            this@NewProductActivity,
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
                            this@NewProductActivity,
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