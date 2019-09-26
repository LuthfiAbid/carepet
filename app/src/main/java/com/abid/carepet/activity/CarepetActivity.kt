package com.abid.carepet.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.abid.carepet.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_carepet.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CarepetActivity : AppCompatActivity() {
    val PERMISSION_RC = 10003
    val REQUEST_CODE_IMAGE = 10002
    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var filePathImage: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carepet)
        fAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        filePathImage = Uri.EMPTY
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "CAREPET ORDER"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val startTime = findViewById<EditText>(R.id.et_time_start)
        val endTime = findViewById<EditText>(R.id.et_time_end)
        imagePet.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                READ_EXTERNAL_STORAGE
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

        et_time_start.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
                .show()
        }

        et_time_end.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                endTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
                .show()
        }

        btn_submit_order.setOnClickListener {
            val name = et_nama_pelanggan.text.toString()
            val startTime = et_time_start.text.toString()
            val endTime = et_time_end.text.toString()
            val catatan = et_note.text.toString()
            val phone = et_nomor_telepon.text.toString()

            if (name.isNotEmpty() || startTime.isNotEmpty() || endTime.isNotEmpty() || catatan.isNotEmpty()) {
                addToFirebase(name, startTime, endTime, catatan, phone)
            } else {
                Toast.makeText(
                    this,
                    "Fill Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        imagePet.alpha = 1f
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE_IMAGE
        )
    }

    private fun addToFirebase(name: String, startTime: String, endTime: String, catatan: String, phone: String) {
        val uid = fAuth.currentUser?.uid
        val orderid = UUID.randomUUID().toString()
        val storageRef: StorageReference =
            storageReference.child("imagesPet/$uid/$orderid.${GetFileExtension(filePathImage)}")
        storageRef.putFile(filePathImage).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid")
                dbRef.child("image").setValue(it.toString())
                dbRef.child("name").setValue(name)
                dbRef.child("userid").setValue(uid)
                dbRef.child("phone").setValue(phone)
                dbRef.child("orderid").setValue(orderid)
                dbRef.child("startTime").setValue(startTime)
                dbRef.child("endTime").setValue(endTime)
                dbRef.child("note").setValue(catatan)
                dbRef.child("status").setValue("In Approve")
            }
            startActivity(Intent(this, HomeActivity::class.java))
            Toast.makeText(
                this,
                "Success Upload",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_RC -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] == PackageManager.PERMISSION_DENIED
                ) {
                    Toast.makeText(
                        this,
                        "Ditolak",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    imageChooser()
                }
            }
        }
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
                        .centerCrop().into(imagePet)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

}

