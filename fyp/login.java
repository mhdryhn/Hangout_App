package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class login extends AppCompatActivity {

    FirebaseAuth mAuth1;
    FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Organizer Account Information");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth1 = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth1.getCurrentUser();
        user = mAuth1.getCurrentUser();
        if(currentUser != null){
            notebookRef.whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()){
                                Intent intent = new Intent(login.this, member_home_pg.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(login.this, organizer_home_pg.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button=findViewById(R.id.btnMember);
        Button button1=findViewById(R.id.btnOrganizer);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(login.this, organizer_login.class);
                startActivity(intent);
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, member_login.class);
                startActivity(intent);
            }

        });
    }

    public void onBackPressed() {
        // Display the login or sign up page again
        Intent intent = new Intent(login.this, loginorsignup_page.class);
        startActivity(intent);
        finish();
    }
}

