package com.o.visitormanagement.repository;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.o.visitormanagement.model.User;

import java.io.File;

public class FirebaseRepository {
    public static FirebaseRepository instance;
    private MutableLiveData<Uri> imageDownloadUrl = new MutableLiveData<>();
    private StorageReference imagesRef;
    private DatabaseReference visitorRef;
    private DatabaseReference suspiciousRef;

    public static FirebaseRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseRepository();
        }
        return instance;
    }

    public FirebaseRepository() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        imagesRef = storageRef.child("user_images");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        visitorRef = database.getReference("visitors");
        suspiciousRef = database.getReference("suspicious_users");
    }

    public void uploadImageFromFIle(File file) {
        Uri uri = Uri.fromFile(file);
        final StorageReference ref = imagesRef.child(file.getName());
        UploadTask uploadTask = ref.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageDownloadUrl.setValue(downloadUri);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    public void uploadUserData(String uid, User user) {
        visitorRef.child(uid).setValue(user);
    }

    public void uploadSuspiciousUserData(String uid, User user) {
        suspiciousRef.child(uid).setValue(user);
    }

    public LiveData<Uri> getDownloadUri() {
        return imageDownloadUrl;
    }

}
