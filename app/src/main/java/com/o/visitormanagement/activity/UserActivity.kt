package com.o.visitormanagement.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.o.visitormanagement.R
import com.o.visitormanagement.databinding.ActivityMainBinding
import com.o.visitormanagement.databinding.ActivityUserBinding
import com.o.visitormanagement.model.User
import com.o.visitormanagement.viewmodel.FirebaseViewModel

class UserActivity : AppCompatActivity() {

    var phoneNumber : String? = null

    private lateinit var binding: ActivityUserBinding

    private lateinit var viewmodel : FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_user
        )

        phoneNumber = intent.getStringExtra("phoneNumber")

        viewmodel = ViewModelProviders.of(this).get(FirebaseViewModel::class.java)
        viewmodel.init()

        val UserUrlObserver = Observer<User> { user ->
            if (user != null) {
                binding.welcome.text = "Welcome Back!\nVisit Count = ${++user.vistCount}"
                viewmodel.incrementVisitCount(user.uid, user.vistCount)
            }
        }
        viewmodel.getUserData(phoneNumber).observe(this, UserUrlObserver)
    }
}
