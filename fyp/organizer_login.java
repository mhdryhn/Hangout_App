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
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QuerySnapshot;

public class organizer_login extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView notuser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Member Account Information");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(organizer_login.this, organizer_home_pg.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.Email_info);
        editTextPassword = findViewById(R.id.Password_info);
        buttonLogin = findViewById(R.id.btnLogin);
        notuser = findViewById(R.id.notauser);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email,password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                //Check if Email and Password is Empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(organizer_login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(organizer_login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Authenticating User
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    notebookRef.whereEqualTo("email", email)
                                        .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.isEmpty()) {
                                                        // Email not found, user is logged in
                                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(organizer_login.this, organizer_home_pg.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // Email found, user is logged out
                                                        FirebaseAuth.getInstance().signOut();
                                                        Toast.makeText(getApplicationContext(), "This email is registered as a Member. We will direct you to the member's login page", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(organizer_login.this, member_login.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(organizer_login.this, "Please check your details and try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //If user has not Registered
        notuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(organizer_login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });
    }
}