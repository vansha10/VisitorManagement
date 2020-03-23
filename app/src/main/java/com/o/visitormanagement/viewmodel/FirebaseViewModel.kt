package com.o.visitormanagement.viewmodel


import android.net.Uri

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.o.visitormanagement.repository.FirebaseRepository

import java.io.File

class FirebaseViewModel : ViewModel() {

    private var firebaseRepository: FirebaseRepository? = null

    val downloadUrl: LiveData<Uri>
        get() = firebaseRepository!!.downloadUri

    fun init() {
        firebaseRepository = FirebaseRepository.getInstance()
    }

    fun uploadImageFromFile(file: File?) {
        firebaseRepository!!.uploadImageFromFIle(file)
    }

}
