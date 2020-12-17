package com.example.rentall

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser!!.uid
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


        val productId = FirebaseDatabase.getInstance().reference.child("Product").push().key
        val productRef =
            FirebaseDatabase.getInstance().reference.child("Product").child(productId!!)
        val userProductRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Product")
                .child(productId)



        activity_new_product_btn_addimg.setOnClickListener {
            selectImage()
        }

        activity_new_product_btn_submit.setOnClickListener {
            uploadData(productId, productRef, userProductRef)
            uploadImage(productId)

            val intent = Intent(this@NewProductActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadData(
        productId: String,
        productRef: DatabaseReference,
        userProductRef: DatabaseReference
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
        productRef.updateChildren(productInfo)

        val eventsInfo2: MutableMap<String, Any> = HashMap()
        eventsInfo2["id"] = productId
        userProductRef.updateChildren(eventsInfo2)
    }

    // Select Image method
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    // Override onActivityResult method
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {

            // Get the Uri of data
            filePath = data.data
            try {

                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                    .getBitmap(
                        contentResolver,
                        filePath
                    )
                activity_new_product_iv_productimg.setImageBitmap(bitmap)
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }

    // UploadImage method
    private fun uploadImage(productId: String) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference
                .child(
                    "images/products/"
                            + productId
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath!!)
                ?.addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@NewProductActivity,
                            "Data Uploaded!!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                ?.addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@NewProductActivity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }
    }
}