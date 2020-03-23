package com.o.visitormanagement.viewmodel;


import androidx.lifecycle.ViewModel;

import com.o.visitormanagement.repository.FirebaseRepository;

import java.io.File;

public class FirebaseViewModel extends ViewModel {

    FirebaseRepository firebaseRepository;

    public void init() {
        firebaseRepository = FirebaseRepository.getInstance();
    }

    public void uploadImageFromFile(File file) {
        firebaseRepository.uploadImageFromFIle(file);
    }

}
