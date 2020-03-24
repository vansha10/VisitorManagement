package com.o.visitormanagement.repository;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.o.visitormanagement.model.User;

import java.io.File;

public class FirebaseRepository {
    public static FirebaseRepository instance;

    private MutableLiveData<Uri> imageDownloadUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> userExists = new MutableLiveData<>();
    private MutableLiveData<User> userData = new MutableLiveData<>();

    private StorageReference imagesRef;
    private DatabaseReference visitorRef;
    private DatabaseReference suspiciousRef;
    private String TAG = "Firebase";

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

        userExists.setValue(null);
        userData.setValue(null);
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

    public void uploadUserData(User user) {
        visitorRef.child(user.getUid()).child("phoneNumber").setValue(user.getPhoneNumber());
        visitorRef.child(user.getUid()).child("photoDownloadUrl").setValue(user.getPhotoDownloadUrl());
        visitorRef.child(user.getUid()).child("visitCount").setValue(user.getVistCount());
    }

    public void uploadSuspiciousUserData(User user) {
        suspiciousRef.child(user.getUid()).setValue(user);
    }

    public LiveData<Boolean> getUserExists(String phoneNumber) {
        Query queries = visitorRef.orderByChild("phoneNumber").equalTo(phoneNumber);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    userExists.setValue(false);
                } else {
                    userExists.setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        queries.addListenerForSingleValueEvent(eventListener);
        return userExists;
    }

    public LiveData<User> getUserData(String phoneNumber) {
        Query queries = visitorRef.orderByChild("phoneNumber").equalTo(phoneNumber);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        String uid = userSnapshot.getKey();
                        String url = userSnapshot.child("photoDownloadUrl").getValue(String.class);
                        Integer count = userSnapshot.child("visitCount").getValue(Integer.class);
                        User user = new User(uid, phoneNumber, url, count);
                        userData.setValue(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        queries.addListenerForSingleValueEvent(eventListener);
        return userData;
    }

    public void incrementVisitCount(String uid, int count) {
        visitorRef.child(uid).child("visitCount").setValue(count);
    }

    public LiveData<Uri> getDownloadUri() {
        return imageDownloadUrl;
    }

    public void setExistsToNull() {
        userExists.setValue(null);
    }

    public void setUserDataToNull() {
        userData.setValue(null);
    }

}
