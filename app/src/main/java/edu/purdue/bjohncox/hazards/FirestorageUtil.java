package edu.purdue.bjohncox.hazards;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FirestorageUtil {

    static FirebaseStorage storage = FirebaseStorage.getInstance();

    public static void uploadImageToStorage(byte[] data, Context c, final OnCompleteListener<Uri> onCompleteListener) {

        FirebaseApp.initializeApp(c);
        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Timestamp(System.currentTimeMillis())));
        final UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("Upload failed");
                onCompleteListener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            System.out.println("isSuccessful failed");
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            System.out.println("GOT THE URI: "+downloadUri);
                            onCompleteListener.onComplete(task);
                        } else {
                            onCompleteListener.onComplete(null);
                            System.out.println("Stuff failed");
                        }
                    }
                });
            }
        });

    }

}
