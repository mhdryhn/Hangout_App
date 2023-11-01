package com.example.fyp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Collection;

public class profile_organizer extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    Button setdp;

    Button save_info;

    FirebaseUser user;

    TextView email;
    TextView number;
    TextView name;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Organizer Account Information");
    private DocumentReference noteRef = db.document("Organizer Account Information/Account");
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Organizer Profile Pictures");
    private Uri imageUri;

    private String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_organizer);
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.btnLogout);
        setdp = findViewById(R.id.changepp);
        email = findViewById(R.id.og_email);
        name = findViewById(R.id.og_name);
        number = findViewById(R.id.og_number);
        user = auth.getCurrentUser();



        CircularImageView profile_pic = findViewById(R.id.profile_pic);
        notebookRef.whereEqualTo("email", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle the error
                            Toast.makeText(profile_organizer .this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Iterate through the documents
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            String profilePicUrl = note.getProfile_pic();

                            // Load the updated profile picture into the button or its ImageView
                            Picasso.get().load(profilePicUrl).into(profile_pic);
                        }
                    }
                });
        save_info = findViewById(R.id.Save_Info);
        final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;
                        profile_pic.setImageURI(result);
                    }
                }
        );

        setdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLauncher.launch("image/*"
                );
            }
        });

        //Getting the Image URL and Saving the Information
        save_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageRef = storageRef.child(System.currentTimeMillis() + "");
                if (imageUri != null) {
                    storageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageurl = uri.toString();
                                        notebookRef.whereEqualTo("email", user.getEmail())
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                            Note note = documentSnapshot.toObject(Note.class);
                                                            note.setProfile_pic(imageurl);
                                                            String documentId = documentSnapshot.getId(); // Get the document ID
                                                            notebookRef.document(documentId).set(note)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(profile_organizer.this, "Profile Picture Set", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(profile_organizer.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                            } else {
                                Toast.makeText(profile_organizer.this , task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        if(user == null){
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }
        else{
            email.setText(user.getEmail());
        }
        //Getting data from firebase
        notebookRef.whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            String name_organizer = note.getName();
                            long number_organizer = note.getNumber();

                            name.setText(name_organizer);
                            number.setText(String.valueOf(number_organizer));
                        }
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}