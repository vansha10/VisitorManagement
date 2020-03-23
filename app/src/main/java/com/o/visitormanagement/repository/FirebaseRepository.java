package com.o.visitormanagement.repository;


import java.io.File;

public class FirebaseRepository {
    public static FirebaseRepository instance;

    public static FirebaseRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseRepository();
        }
        return instance;
    }

    public void uploadImageFromFIle(File file) {

    }

}
