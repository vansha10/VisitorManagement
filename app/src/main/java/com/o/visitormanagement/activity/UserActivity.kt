package com.o.visitormanagement.activity

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.o.visitormanagement.databinding.ActivityMainBinding
import com.o.visitormanagement.databinding.ActivityUserBinding
import com.o.visitormanagement.model.User
import com.o.visitormanagement.viewmodel.FirebaseViewModel
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide


class UserActivity : AppCompatActivity() {

    var phoneNumber : String? = null
    var userData : User? = null

    private lateinit var binding: ActivityUserBinding

    private lateinit var viewmodel : FirebaseViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            com.o.visitormanagement.R.layout.activity_user
        )

        phoneNumber = intent.getStringExtra("phoneNumber")
        userData = intent.getSerializableExtra("user") as User?

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewModel::class.java)
        viewmodel.init()
        viewmodel.setUserDataToNull()

        if (phoneNumber != null) {
            val userUrlObserver = Observer<User> { user ->
                if (user != null) {
                    binding.welcome.text = "Welcome Back!\nVisit Count = ${++user.vistCount}"
                    viewmodel.incrementVisitCount(user.uid, user.vistCount)
                    updateUI(user.phoneNumber, user.photoDownloadUrl)
                }
            }
            viewmodel.getUserData(phoneNumber).observe(this, userUrlObserver)
        } else {
            if (userData != null) {
                binding.welcome.text = "Welcome Back!\nVisit Count = ${userData!!.vistCount}"
                updateUI(userData!!.phoneNumber, userData!!.photoDownloadUrl)
            }
        }
    }

    private fun updateUI(phoneNumber : String, url : String) {
        binding.userPhone.text = phoneNumber

        Glide.with(this).load(url).placeholder(
            getDrawable(com.o.visitormanagement.R.drawable.ic_person_black_160dp)).into(binding.userPhoto)
    }

    override fun onBackPressed() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("EXIT", true)
        startActivity(intent)
    }

    override fun onStop() {
        FirebaseAuth.getInstance().signOut()
        super.onStop()
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().signOut()
        super.onDestroy()
    }

    override fun onStart() {
        viewmodel.setUserDataToNull()
        super.onStart()
    }

    override fun onResume() {
        viewmodel.setUserDataToNull()
        super.onResume()
    }

    override fun onRestart() {
        viewmodel.setUserDataToNull()
        super.onRestart()
    }
}
