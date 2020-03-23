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

    fun uploadUserData(user : User) {
        firebaseRepository!!.uploadUserData(user)
    }

    fun uploadSuspiciousUserData(user: User) {
        firebaseRepository!!.uploadSuspiciousUserData(user)
    }

    fun checkUserExists(phoneNumber : String) {
        firebaseRepository!!.checkIfUserExists(phoneNumber)
    }

    fun getUserData(phoneNumber: String?) : LiveData<User>{
        return firebaseRepository!!.getUserData(phoneNumber)
    }

    fun incrementVisitCount(uid : String, count : Int) {
        firebaseRepository!!.incrementVisitCount(uid, count)
    }
}
