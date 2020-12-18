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
import java.io.IOException


class EditProductActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 22
    private var filePath: Uri? = null
    private lateinit var storageRef: StorageReference

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""
        val productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)
        storageRef = FirebaseStorage.getInstance().reference.child("images/products/$productId")

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
            uploadData(productRef)
            uploadImage(productId)

            val intent = Intent(this@EditProductActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_edit_product_btn_delete.setOnClickListener{
            productRef.removeValue()
            storageRef.delete()
                    val move = Intent(this@EditProductActivity, UserAccountActivity::class.java)
                    startActivity(move)
        }
    }

    private fun uploadData(productRef: DatabaseReference) {
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
                activity_edit_product_iv_productimg.setImageBitmap(bitmap)
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

            // adding listeners on upload
            // or failure of image
            storageRef.putFile(filePath!!)
                ?.addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@EditProductActivity,
                            "Data Uploaded!!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnFailureListener { e -> // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            this@EditProductActivity,
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