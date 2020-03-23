package com.o.visitormanagement.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.o.visitormanagement.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import java.io.File
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.o.visitormanagement.R
import com.o.visitormanagement.model.User
import com.o.visitormanagement.viewmodel.FirebaseViewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var verificationActive: Boolean = false
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String? = null
    private val TAG = "MainActivity"

    private val CAMERA_REQUEST = 1

    private lateinit var binding: ActivityMainBinding
    private lateinit var phoneNumber : String
    private var imageFile : File? = null
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var viewmodel : FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        binding.getPhotoButton.setOnClickListener { getPhoto() }
        binding.submitButton.setOnClickListener {
            if (!verificationActive)
                checkDetails()
            else
                checkCode()
        }

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewModel::class.java)
        viewmodel.init()

        initializeCallbacks()
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
            phoneNumber = "+91$phoneNumber"
            checkIfUserExists(phoneNumber)
        }
    }

    private fun checkCode() {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, binding.codeEditText.text.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun initializeCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:$credential")
                // for auto verification
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                Log.w(TAG, "onVerificationFailed", e)
                binding.phoneLayout.visibility = View.VISIBLE
                binding.codeLayout.visibility = View.GONE

                verificationActive = false

                binding.progressLayout.visibility = View.INVISIBLE

                Snackbar.make(
                    binding.mainLayout,
                    getString(R.string.verification_failed),
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                binding.phoneLayout.visibility = View.GONE
                binding.codeLayout.visibility = View.VISIBLE
                binding.progressLayout.visibility = View.GONE

                verificationActive = true


            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        binding.progressLayout.visibility = View.VISIBLE
        binding.status.text = getString(R.string.validating_otp)
        binding.submitButton.visibility = View.GONE
        binding.codeLayout.visibility = View.GONE

        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    verificationActive = false

                    Snackbar.make(
                        binding.mainLayout,
                        getString(R.string.success_msg),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    uploadImage(user!!.uid)
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                        uploadSuspiciousUserImage(phoneNumber)
                    }
                }
            }
    }

    private fun getPhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, CAMERA_REQUEST)
    }


    private fun checkIfUserExists(phoneNumber: String) {
        viewmodel.checkUserExists(phoneNumber)
        val userExistsObserver = Observer<Boolean> { exists ->
            if (exists != null) {
                if (!exists) {
                    sendOTP()
                } else {
                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                }
            }
        }
        viewmodel.userExists.observe(this, userExistsObserver)

    }

    private fun sendOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        binding.progressLayout.visibility = View.VISIBLE
        binding.status.text = getString(R.string.sending_otp)
        binding.phoneLayout.visibility = View.GONE
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

    private fun uploadImage(uid : String) {
        binding.status.text = getString(R.string.uploading_user_data)
        viewmodel.uploadImageFromFile(imageFile)
        var downloadUrl : String
        val imageDownloadUrlObserver = Observer<Uri> { uri ->
            downloadUrl = uri.toString()
            val user = User(uid, phoneNumber, downloadUrl, 1)
            uploadUserData(user)

        }
        viewmodel.downloadUrl.observe(this, imageDownloadUrlObserver)
    }

    private fun uploadUserData(user: User) {
        binding.status.text = getString(R.string.almost_there)
        viewmodel.uploadUserData(user)
        binding.progressLayout.visibility = View.GONE
        Snackbar.make(
            binding.mainLayout,
            getString(R.string.visitor_saved),
            Snackbar.LENGTH_SHORT
        ).show()
        //TODO: new activity
    }

    private fun uploadSuspiciousUserImage(uid : String) {
        viewmodel.uploadImageFromFile(imageFile)
        var downloadUrl : String
        val imageDownloadUrlObserver = Observer<Uri> { uri ->
            downloadUrl = uri.toString()
            val user = User(uid, phoneNumber, downloadUrl, 0)
            uploadSuspiciousUserData(user)

        }
        viewmodel.downloadUrl.observe(this, imageDownloadUrlObserver)
    }

    private fun uploadSuspiciousUserData(user: User) {
        binding.status.text = getString(R.string.almost_there)
        viewmodel.uploadSuspiciousUserData(user)
        binding.progressLayout.visibility = View.GONE
        Snackbar.make(
            binding.mainLayout,
            getString(R.string.invalid_code_msg),
            Snackbar.LENGTH_SHORT
        ).show()

        binding.codeLayout.visibility = View.GONE
        binding.phoneLayout.visibility = View.VISIBLE
        binding.submitButton.visibility = View.VISIBLE
        verificationActive = false
    }
}
