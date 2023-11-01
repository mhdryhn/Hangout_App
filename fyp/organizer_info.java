package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class organizer_info extends AppCompatActivity {
    private static final String TAG = "organizer_info";

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NUMBER = "number";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Organizer Account Information");
    private DocumentReference noteRef = db.document("Organizer Account Information/Account");

    EditText editTextEmail1, editTextPassword, editTextName, editTextNumber, editTextRytpPassword;
    Button buttonReg;
    FirebaseAuth mAuth;

    CheckBox tandc;

    String[] item = {"Educational", "Leisure", "Sports", "Mental Health", "Conferences", "Other"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    //Save_Info Method
    public void SaveInfo(View v) {
        String name = editTextName.getText().toString();
        String email = editTextEmail1.getText().toString();
        long num = Long.parseLong(editTextNumber.getText().toString());

        Note note = new Note(name,num,email,"https://firebasestorage.googleapis.com/v0/b/hngout-fyp.appspot.com/o/Organizer%20Profile%20Pictures%2F1689755535089?alt=media&token=addb1c78-bacf-4ea6-85a8-26cc89be3219");
        notebookRef.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(organizer_info.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }

    //Email check
    public static boolean valEmail(String input)
    {
        String emailcheck = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailcheck,Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(input);
        return matcher.find();
    }

    //Checks if the user is already logged in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(organizer_info.this, organizer_home_pg.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_info);


        //Creating a User Account
        mAuth = FirebaseAuth.getInstance();
        editTextEmail1 = findViewById(R.id.Email_info);
        editTextPassword = findViewById(R.id.Password_info);
        editTextName = findViewById(R.id.Name_info);
        editTextNumber = findViewById(R.id.Number_info);
        editTextRytpPassword = findViewById(R.id.Cpassword_info);
        buttonReg = findViewById(R.id.btnCreateAccount);
        tandc = findViewById(R.id.cb1);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password, name, rtyp_password;
                long number;
                email = String.valueOf(editTextEmail1.getText());
                password = String.valueOf(editTextPassword.getText());
                rtyp_password = String.valueOf(editTextRytpPassword.getText());
                name = String.valueOf(editTextName.getText());
                String numberString = editTextNumber.getText().toString();


                //Check if Fields are Empty

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(organizer_info.this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(organizer_info.this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (valEmail(email) == false){
                    Toast.makeText(organizer_info.this, "Your Email is the incorrect format. Please check and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(numberString)) {
                    // The input is empty, show an error message or handle it appropriately
                    Toast.makeText(organizer_info.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                number = Long.parseLong(numberString);

                //Number Validity check (l is added to number to signify it is a number of long datatype)
                if(number == 0 || number < 6000000000l || number > 700000000000l){
                    Toast.makeText(organizer_info.this, "Please type in a valid number with the format - 60xxxxxxxxx", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(organizer_info.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(rtyp_password)) {
                    Toast.makeText(organizer_info.this, "Please Confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Email Validity check


                //Password confirmation check
                if (password.equals(rtyp_password) == false){
                    Toast.makeText(organizer_info.this, "Passwords not matching. Please check and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(tandc.isChecked() == false){
                    Toast.makeText(organizer_info.this, "Please agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Registering the User
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(organizer_info.this, "Account Created",
                                            Toast.LENGTH_SHORT).show();
                                    SaveInfo(buttonReg);
                                    //Opening Set_Location Page after successful registration
                                    Intent intent = new Intent(organizer_info.this, organizer_home_pg.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign up fails, display a message to the user
                                    Toast.makeText(organizer_info.this, "Your Account as has not been created, please check your details and try again",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        //Assigning Activities to Button
        Button button1=findViewById(R.id.btnReturn);
        //Open Activity on Button1
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(organizer_info.this, signup.class);
                startActivity(intent);

            }

        });
    }

}