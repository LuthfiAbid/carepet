package com.abid.carepet.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    lateinit var preferences: Pref
    val REQUEST_CODE_IMAGE = 10002
    val PERMISSION_RC = 10003
    var value = 0.0
    lateinit var filePathImage: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    var photo = null

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap: Bitmap = MediaStore
                        .Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this).load(bitmap)
                        .override(250, 250)
                        .centerCrop().into(imageplaceholder)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        preferences = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        setSupportActionBar(toolbarEdit)
        supportActionBar!!.title = "EDIT PROFILE"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        imageplaceholder.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), PERMISSION_RC
                        )
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }

        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value == null) {
                        Glide.with(this@EditProfileActivity).load(R.drawable.user)
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(imageplaceholder)
                    } else {
                        Glide.with(this@EditProfileActivity).load(p0.value.toString())
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(imageplaceholder)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_nama_edit_profile.setText(p0.value.toString())
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("phone").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_phone_edit_profile.setText(p0.value.toString())
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        btn_saveEdit.setOnClickListener {
            val uidUser = fAuth.currentUser?.uid
            dbRef = FirebaseDatabase.getInstance().reference
            val eteditnamao = tv_nama_edit_profile.text.toString()
            val eto = tv_phone_edit_profile.text.toString()
            try {
                val storageRef: StorageReference = storageReference
                    .child("profile/$uidUser/${preferences.getUIDD()}.${GetFileExtension(filePathImage)}")
                storageRef.putFile(filePathImage).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener {
                        dbRef.child("dataUser/$uidUser/profile").setValue(it.toString())
                    }
                }.addOnFailureListener {
                    Log.e("TAG_ERROR", it.message)
                }.addOnProgressListener { taskSnapshot ->
                    value = (100.0 * taskSnapshot
                        .bytesTransferred / taskSnapshot.totalByteCount)
                }
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
            }
            dbRef.child("dataUser/$uidUser/name").setValue(eteditnamao)
            dbRef.child("dataUser/$uidUser/phone").setValue(eto)
            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}