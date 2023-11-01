package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fyp.databinding.ActivityMemberHomePgBinding;
import com.example.fyp.databinding.ActivityOrganizerHomePgBinding;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class member_home_pg extends AppCompatActivity {

    ActivityMemberHomePgBinding binding;

    BottomNavigationView nav;

    FirebaseUser user;

    FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Member Account Information");

    @Override
    public void onBackPressed() {
        // Do nothing to prevent going back
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemberHomePgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nav = findViewById(R.id.nav_view);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        replaceFragment(new explore_fragment());
        nav.setSelectedItemId(R.id.explore);

        CircularImageView button= findViewById(R.id.profile_member);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(member_home_pg.this, profile_member.class);
                startActivity(intent);
            }
        });

        notebookRef.whereEqualTo("email", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle the error
                            Toast.makeText(member_home_pg .this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Iterate through the documents
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note_member note = documentSnapshot.toObject(Note_member.class);
                            String profilePicUrl = note.getProfile_pic();

                            // Load the updated profile picture into the button or its ImageView
                            Picasso.get().load(profilePicUrl).into(button);
                        }
                    }
                });


        binding.navView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home)
                replaceFragment(new home_fragment());
            else if (item.getItemId() == R.id.events) {
                replaceFragment(new memberevents_fragment());
            }
            else if ((item.getItemId() == R.id.explore)){
                replaceFragment(new explore_fragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }
}