package com.o.visitormanagement

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.o.visitormanagement.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.View
import java.io.File
import androidx.exifinterface.media.ExifInterface




class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST = 1

    private lateinit var binding: ActivityMainBinding
    private lateinit var phoneNumber : String
    private var imageFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.getPhotoButton.setOnClickListener { getPhoto() }
        binding.submitButton.setOnClickListener { checkDetails() }
    }

    private fun getPhoto() {
        val intent : Intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun checkDetails() {
        var valid = true
        phoneNumber = binding.phoneEditText.text.toString()
        if (phoneNumber.length != 10) {
            valid = false
            Snackbar.make(
                binding.mainLayout,
                R.string.phoneNumberErrorMessage,
                Snackbar.LENGTH_SHORT
            ).show()
        } else if (imageFile == null) {
            valid = false
            Snackbar.make(
                binding.mainLayout,
                getString(R.string.picture_error),
                Snackbar.LENGTH_SHORT
            ).show()
        }
        if (valid) {
            //TODO: authenticate
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val pictureFile = data?.extras?.get("file") as File
                setImage(pictureFile)
            }
        }
    }

    private fun setImage(pictureFile : File) {
        val imageBitmap : Bitmap = BitmapFactory.decodeFile(pictureFile.absolutePath)

        binding.photoImageview.setImageBitmap(imageBitmap)
        binding.photoImageview.visibility = View.VISIBLE

        imageFile = pictureFile
    }
}
