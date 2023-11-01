package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class loginorsignup_page extends AppCompatActivity {


    FirebaseAuth mAuth1;
    FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Organizer Account Information");


    @Override
    public void onBackPressed() {
        // Do nothing to prevent going back
    }

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
                                Intent intent = new Intent(loginorsignup_page.this, member_home_pg.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(loginorsignup_page.this, organizer_home_pg.class);
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
        setContentView(R.layout.activity_loginorsignup_page);

        Button button=findViewById(R.id.btnSignUp);
        Button button1=findViewById(R.id.btnLogin);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(loginorsignup_page.this, login.class);
                startActivity(intent);
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(loginorsignup_page.this, signup.class);
                startActivity(intent);
            }

        });

    }
}