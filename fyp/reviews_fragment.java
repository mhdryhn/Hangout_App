package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reviews_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reviews_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //To retrieve data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Review");

    //ArrayList for the recycler view
    ArrayList<Note_review> reviewsdatamodel = new ArrayList<>();
    ArrayList<String> ReviewNameList = new ArrayList<>();
    ArrayList<String> ReviewContentList = new ArrayList<>();
    ArrayList<Float> RatingValueList = new ArrayList<>();

    ArrayList<String> EventIDList = new ArrayList<>();

    //To get user email
    FirebaseUser user;
    FirebaseAuth auth;

    public reviews_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reviews_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static reviews_fragment newInstance(String param1, String param2) {
        reviews_fragment fragment = new reviews_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.Review_RecyclerView);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        notebookRef.whereEqualTo("organizer_email", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Note_review note = documentSnapshot.toObject(Note_review.class);
                                ReviewNameList.add(note.getUser_name());
                                RatingValueList.add(note.getEvent_rating());
                                ReviewContentList.add(note.getReview_content());
                                EventIDList.add(note.getEvent_id());
                        }
                        for(int i = 0; i < ReviewNameList.size(); i++){
                            reviewsdatamodel.add(new Note_review(ReviewContentList.get(i), RatingValueList.get(i), EventIDList.get(i),ReviewNameList.get(i)));
                        }
                        int datamodelsize = reviewsdatamodel.size();
                        if (datamodelsize != 0){
                            TextView label1 = view.findViewById(R.id.noreviews);
                            label1.setVisibility(view.INVISIBLE);
                        }

                        RED_RecyclerViewAdapter adapter = new RED_RecyclerViewAdapter(requireContext(), reviewsdatamodel);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                });
        return view;
    }
}