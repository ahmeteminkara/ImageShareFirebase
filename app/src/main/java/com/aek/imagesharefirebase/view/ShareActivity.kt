package com.aek.imagesharefirebase.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aek.imagesharefirebase.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_share.*
import java.lang.Exception
import java.util.*

class ShareActivity : AppCompatActivity() {

    companion object {
        const val keyEmail = "userEmail"
        const val keyImageUrl = "imageUrl"
        const val keyComment = "comment"
        const val keyDate = "date"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore


    var imageUri: Uri? = null
    var imageBitmap: Bitmap? = null
    val codePermissionStorage = 0
    val codeRequestGallery = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        val actionBar = supportActionBar
        actionBar?.let { bar ->
            bar.title = "Share Photo"
            bar.setDisplayHomeAsUpEnabled(true)
            bar.setDisplayHomeAsUpEnabled(true)
        }


        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()

    }

    fun selectImage(view: View) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            //izin verilmedi
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                codePermissionStorage
            )

        } else {
            //galeriye git
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, codeRequestGallery)

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == codePermissionStorage) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin aldık
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, codeRequestGallery)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == codeRequestGallery && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data

            try {

                if (imageUri != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(contentResolver, imageUri!!)
                        imageBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(imageBitmap)
                    } else {
                        imageBitmap =
                            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        imageView.setImageBitmap(imageBitmap)
                    }
                }


            } catch (e: Exception) {

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun sharePhoto(view:View) {

        val uuid = UUID.randomUUID()
        val reference = storage.reference
        val imageName = "$uuid.jpg"

        val visualReference = reference.child("images").child(imageName)

        if (imageUri != null) {
            visualReference.putFile(imageUri!!).addOnSuccessListener { task ->

                val savedImageReference = reference.child("images").child(imageName)

                savedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val userEmail = auth.currentUser!!.email.toString()
                    val comment = etImageText.text.toString()
                    val date = Timestamp.now()

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put(keyEmail, userEmail)
                    postHashMap.put(keyImageUrl, imageUrl)
                    postHashMap.put(keyComment, comment)
                    postHashMap.put(keyDate, date)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                }


            }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}