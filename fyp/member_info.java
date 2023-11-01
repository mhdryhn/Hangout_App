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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class member_info extends AppCompatActivity {
    private static final String TAG = "organizer_info";

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NUMBER = "number";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Member Account Information");
    private DocumentReference noteRef = db.document("Member Account Information/Account");
    EditText editTextEmail, editTextPassword, editTextName, editTextNumber, editTextRytpPassword, editTextAge;

    RadioButton malebutton, femalebutton;

    CheckBox tandc;
    Button buttonReg;
    FirebaseAuth mAuth1;


    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    //Save_Info Method
    public void SaveInfoMember(View v) {
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        long num = Long.parseLong(editTextNumber.getText().toString());
        int age = Integer.parseInt(editTextAge.getText().toString());
        String gender = "male";
        if (malebutton.isChecked()) {
            gender = malebutton.getText().toString();
        } else if (femalebutton.isChecked()) {
            gender = femalebutton.getText().toString();
        }

        Note_member note = new Note_member(name,num,email,gender,"https://firebasestorage.googleapis.com/v0/b/hngout-fyp.appspot.com/o/Member%20Profile%20Pictures%2F1688823030459?alt=media&token=efd85a07-a887-45c4-b8d6-d29c60c2d9b6",age, null);
        notebookRef.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(member_info.this, "Error!", Toast.LENGTH_SHORT).show();
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
        FirebaseUser currentUser = mAuth1.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(member_info.this, organizer_home_pg.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        //Creating a User Account
        mAuth1 = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.Email_info);
        editTextName = findViewById(R.id.Name_info);
        editTextPassword = findViewById(R.id.Password_info);
        editTextAge = findViewById(R.id.age_info1);
        editTextNumber = findViewById(R.id.Number_info);
        editTextRytpPassword = findViewById(R.id.Cpassword_info);
        buttonReg = findViewById(R.id.btnsetLocation);
        malebutton = findViewById(R.id.male);
        femalebutton = findViewById(R.id.female);
        tandc = findViewById(R.id.cb1);
        RadioGroup radioGroup = findViewById(R.id.Gender_Group);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password, name, rtyp_password;
                int age;
                long number;

                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                rtyp_password = String.valueOf(editTextRytpPassword.getText());
                name = String.valueOf(editTextName.getText());
                String numberString = editTextNumber.getText().toString();
                String ageString = editTextAge.getText().toString();







                //Check if Fields are Empty

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(member_info.this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(ageString)) {
                    // The input is empty, show an error message or handle it appropriately
                    Toast.makeText(member_info.this, "Please enter a valid Age", Toast.LENGTH_SHORT).show();
                    return;
                }
                age = Integer.parseInt(ageString);

                //Age Check
                if(age > 110 || age < 16){
                    Toast.makeText(member_info.this, "Sorry, this app is can only be used by individuals over the age of 16 and below 110", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if at least one RadioButton is selected
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    // None selected, show an error message or perform appropriate action
                    Toast.makeText(member_info.this, "Please select an option for gender", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(member_info.this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Email Validity check
                if (valEmail(email) == false){
                    Toast.makeText(member_info.this, "Your Email is the incorrect format. Please check and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(numberString)) {
                    // The input is empty, show an error message or handle it appropriately
                    Toast.makeText(member_info.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
                number = Long.parseLong(numberString);

                //Number Validity check (l is added to number to signify it is a number of long datatype)
                if(number == 0 || number < 6000000000l || number > 700000000000l){
                    Toast.makeText(member_info.this, "Please type in a valid number with the format - 60xxxxxxxxx", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(member_info.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(rtyp_password)) {
                    Toast.makeText(member_info.this, "Please Confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Password confirmation check
                if (password.equals(rtyp_password) == false){
                    Toast.makeText(member_info.this, "Passwords not matching. Please check and try again.", Toast.LENGTH_SHORT).show();
                    return;
                }



                //Terms and Condition Check
                if(tandc.isChecked() == false){
                    Toast.makeText(member_info.this, "Please agree to the Terms and Conditions.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Registering the User
                mAuth1.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(member_info.this, "Proceed to set your Location.",
                                            Toast.LENGTH_SHORT).show();
                                    SaveInfoMember(buttonReg);
                                    //Opening Set_Location Page after successful registration
                                    Intent intent = new Intent(member_info.this, Set_Location.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign up fails, display a message to the user
                                    Toast.makeText(member_info.this, "Your Account as has not been created, please check your details and try again",
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

                Intent intent = new Intent(member_info.this, signup.class);
                startActivity(intent);

            }

        });
    }

}