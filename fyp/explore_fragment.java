package com.example.fyp;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
 * Use the {@link explore_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class explore_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Event Information");
    private CollectionReference notebookRef2 = db.collection("Tickets");
    private DocumentReference noteRef = db.document("Event Information/New Event");

    private boolean isPageLoaded = false;

    //ArrayList for the recycler view
    ArrayList<event_data> eventdatamodel = new ArrayList<>();
    ArrayList<String> EventNameList = new ArrayList<>();
    ArrayList<String> EventDateList = new ArrayList<>();
    ArrayList<String> EventTimeList = new ArrayList<>();
    ArrayList<String> EventImagesList = new ArrayList<>();

    ArrayList<String> doc_idList = new ArrayList<>();

    ArrayList<String> TicketIDList = new ArrayList<>();

    //To get user email
    FirebaseUser user;
    FirebaseAuth auth;


    public explore_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment explore_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static explore_fragment newInstance(String param1, String param2) {
        explore_fragment fragment = new explore_fragment();
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
        View view = inflater.inflate(R.layout.fragment_explore_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.MemberEvent_RecyclerView);
        Log.d("Firestore", "DocumentSnapshot successfully retrieved223");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        notebookRef2.whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            notebookRef.whereGreaterThan("time_value", Calendar.getInstance().getTimeInMillis())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.d("Firestore", "DocumentSnapshot successfully retrieved");
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                doc_idList.add(documentSnapshot.getId());
                                                EventNameList.add(note.getEvent_name());
                                                EventDateList.add(note.getDate());
                                                EventTimeList.add(note.getTime());
                                                EventImagesList.add(note.getImageurl());
                                            }
                                            for (int i = 0; i < EventNameList.size(); i++) {
                                                eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventImagesList.get(i), doc_idList.get(i)));
                                            }
                                            int datamodelsize = eventdatamodel.size();
                                            Log.d("List Size", "The size of EventList is22: " + datamodelsize);

                                            int layoutResourceID = R.layout.activity_memberevent_recyclerview_row;
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel, layoutResourceID);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member_ticket ticket = documentSnapshot.toObject(Note_member_ticket.class);
                                TicketIDList.add(ticket.getEvent_id());
                            }
                            notebookRef.whereGreaterThan("time_value", Calendar.getInstance().getTimeInMillis())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.d("Firestore", "DocumentSnapshot successfully retrieved");
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                note.setDocumentId(documentSnapshot.getId());
                                                String event_id = note.getDocumentId();
                                                boolean isTicketID = false; // Flag variable
                                                for (int i = 0; i < TicketIDList.size(); i++) {
                                                    if (event_id.equals(TicketIDList.get(i))) {
                                                        isTicketID = true; // Set flag to true
                                                        break;
                                                    }
                                                }

                                                if (!isTicketID) {
                                                    // Add event details only when the event ID is not in TicketIDList
                                                    doc_idList.add(documentSnapshot.getId());
                                                    EventNameList.add(note.getEvent_name());
                                                    EventDateList.add(note.getDate());
                                                    EventTimeList.add(note.getTime());
                                                    EventImagesList.add(note.getImageurl());
                                                }
                                            }
                                            for (int i = 0; i < EventNameList.size(); i++) {
                                                eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventImagesList.get(i), doc_idList.get(i)));
                                            }
                                            int datamodelsize = eventdatamodel.size();
                                            Log.d("List Size", "The size of EventList is22: " + datamodelsize);

                                            int layoutResourceID = R.layout.activity_memberevent_recyclerview_row;
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel, layoutResourceID);
                                            recyclerView.setAdapter(adapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
        return view;
    }
}



