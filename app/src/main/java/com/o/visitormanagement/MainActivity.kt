package com.o.visitormanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.o.visitormanagement.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.getPhotoButton.setOnClickListener { getPhoto() }
        binding.submitButton.setOnClickListener { checkDetails() }
    }

    private fun getPhoto() {

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
        }
        if (valid) {
            //TODO: authenticate
        }
    }
}
