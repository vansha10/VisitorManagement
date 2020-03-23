package com.o.visitormanagement.viewmodel


import android.net.Uri

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.o.visitormanagement.model.User

import com.o.visitormanagement.repository.FirebaseRepository

import java.io.File

class FirebaseViewModel : ViewModel() {

    private var firebaseRepository: FirebaseRepository? = null

    val downloadUrl: LiveData<Uri>
        get() = firebaseRepository!!.downloadUri

    val userExists : LiveData<Boolean>
        get() = firebaseRepository!!.userExists

    fun init() {
        firebaseRepository = FirebaseRepository.getInstance()
    }

    fun uploadImageFromFile(file: File?) {
        firebaseRepository!!.uploadImageFromFIle(file)
    }

    fun uploadUserData(uid : String, user : User) {
        firebaseRepository!!.uploadUserData(uid, user)
    }

    fun uploadSuspiciousUserData(uid: String, user: User) {
        firebaseRepository!!.uploadSuspiciousUserData(uid, user)
    }

    fun checkUserExists(phoneNumber : String) {
        firebaseRepository!!.checkIfUserExists(phoneNumber)
    }


}
