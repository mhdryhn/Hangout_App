package com.example.fyp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myevents_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myevents_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isPageLoaded = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListenerRegistration snapshotListener;

    //To retrieve data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Event Information");
    private DocumentReference noteRef = db.document("Event Information/New Event");

    //ArrayList for the recycler view
    ArrayList<event_data> eventdatamodel = new ArrayList<>();
    ArrayList<String> EventNameList = new ArrayList<>();
    ArrayList<String> EventDateList = new ArrayList<>();
    ArrayList<String> EventTimeList = new ArrayList<>();
    ArrayList<String> EventMembersList = new ArrayList<>();
    ArrayList<String> EventImagesList = new ArrayList<>();
    ArrayList<String> doc_idList = new ArrayList<>();

    String event_id;

    //To get user email
    FirebaseUser user;
    FirebaseAuth auth;
    public myevents_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment myevents_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static myevents_fragment newInstance(String param1, String param2) {
        myevents_fragment fragment = new myevents_fragment();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (snapshotListener != null) {
            snapshotListener.remove();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myevents_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.Event_RecyclerView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        snapshotListener = notebookRef
                .whereEqualTo("organizer_email", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle any errors
                            Log.e("Firestore", "Error fetching events: ", e);
                            return;
                        }

                        // Clear the existing event data
                        doc_idList.clear();
                        EventNameList.clear();
                        EventDateList.clear();
                        EventTimeList.clear();
                        EventMembersList.clear();
                        EventImagesList.clear();
                        eventdatamodel.clear();

                        // Iterate through the query results and populate the event data again
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note_Event note = documentSnapshot.toObject(Note_Event.class);
                            if (note.getTime_value() > Calendar.getInstance().getTimeInMillis()) {
                                doc_idList.add(documentSnapshot.getId());
                                EventNameList.add(note.getEvent_name());
                                EventDateList.add(note.getDate());
                                EventTimeList.add(note.getTime());
                                EventImagesList.add(note.getImageurl());
                                EventMembersList.add(note.getCurrent_members() + "/" + note.getMax_members());
                            }
                        }

                        // Repopulate the eventdatamodel list with the updated event data
                        for (int i = 0; i < EventNameList.size(); i++) {
                            eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventMembersList.get(i), EventImagesList.get(i), doc_idList.get(i)));
                        }
                        int datamodelsize = eventdatamodel.size();
                        if (datamodelsize != 0){
                            TextView label1 = view.findViewById(R.id.noreviews);
                            label1.setVisibility(view.INVISIBLE);
                        }
                        // Update the RecyclerView adapter with the new event data
                        ED_RecyclerViewAdapter adapter = new ED_RecyclerViewAdapter(requireContext(), eventdatamodel, event_id);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    }
                });

        return view;
    }
}
