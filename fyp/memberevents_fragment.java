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
 * Use the {@link memberevents_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class memberevents_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //ArrayList for the recycler view
    ArrayList<event_data> eventdatamodel = new ArrayList<>();
    ArrayList<String> EventNameList = new ArrayList<>();
    ArrayList<String> EventDateList = new ArrayList<>();
    ArrayList<String> EventTimeList = new ArrayList<>();
    ArrayList<String> docId_List = new ArrayList<>();

    ArrayList<event_data> eventdatamodel1 = new ArrayList<>();
    ArrayList<String> EventNameList1 = new ArrayList<>();
    ArrayList<String> EventDateList1 = new ArrayList<>();
    ArrayList<String> EventTimeList1 = new ArrayList<>();
    ArrayList<String> EventImagesList = new ArrayList<>();
    ArrayList<String> EventImagesList1 = new ArrayList<>();
    ArrayList<String> docId_List1 = new ArrayList<>();


    ArrayList<String> Doc_IdList = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Tickets");
    private CollectionReference notebookRef2 = db.collection("Event Information");
    private DocumentReference noteRef = db.document("Event Information/New Event");

    public memberevents_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment memberevents_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static memberevents_fragment newInstance(String param1, String param2) {
        memberevents_fragment fragment = new memberevents_fragment();
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
        View view = inflater.inflate(R.layout.fragment_memberevents_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.MemberMyEvent_RecyclerView);
        RecyclerView recyclerView2 = view.findViewById(R.id.MemberMyEventPast_RecyclerView);
        FirebaseAuth auth;
        FirebaseUser user;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        notebookRef.whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note_member_ticket note = documentSnapshot.toObject(Note_member_ticket.class);
                            Doc_IdList.add(note.getEvent_id());
                        }

                        notebookRef2.whereGreaterThan("time_value", Calendar.getInstance().getTimeInMillis())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String event_id;
                                        Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                        note.setDocumentId(documentSnapshot.getId());
                                        event_id = note.getDocumentId();

                                        for (String DocId : Doc_IdList) {
                                            if (DocId.equals(event_id)) {
                                                docId_List.add(documentSnapshot.getId());
                                                EventNameList.add(note.getEvent_name());
                                                EventDateList.add(note.getDate());
                                                EventTimeList.add(note.getTime());
                                                EventImagesList.add(note.getImageurl());

                                                Log.d("Loop Ran", DocId);
                                            } else {
                                                Log.d("Loop didnt run", DocId);
                                            }
                                        }
                                    }
                                    for (int i = 0; i < EventNameList.size(); i++) {
                                        eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventImagesList.get(i), docId_List.get(i)));
                                    }
                                    int datamodelsize = eventdatamodel.size();
                                    if (datamodelsize != 0){
                                        TextView label1 = view.findViewById(R.id.layover1);
                                        label1.setVisibility(view.INVISIBLE);
                                    }


                                    TED_RecyclerViewAdapter adapter = new TED_RecyclerViewAdapter(getContext(), eventdatamodel);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    adapter.notifyDataSetChanged();

                                }
                            });

                        notebookRef2.whereLessThan("time_value", Calendar.getInstance().getTimeInMillis())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String event_id;
                                            Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                            note.setDocumentId(documentSnapshot.getId());
                                            event_id = note.getDocumentId();

                                            for (String DocId : Doc_IdList) {
                                                if (DocId.equals(event_id)) {
                                                    docId_List1.add(documentSnapshot.getId());
                                                    EventNameList1.add(note.getEvent_name());
                                                    EventDateList1.add(note.getDate());
                                                    EventTimeList1.add(note.getTime());
                                                    EventImagesList1.add(note.getImageurl());
                                                    Log.d("Loop Ran", DocId);
                                                } else {
                                                    Log.d("Loop didnt run", DocId);
                                                }
                                            }
                                    }
                                    for (int i = 0; i < EventNameList1.size(); i++) {
                                        eventdatamodel1.add(new event_data(EventNameList1.get(i), EventDateList1.get(i), EventTimeList1.get(i), EventImagesList1.get(i), docId_List1.get(i)));
                                    }
                                    int datamodelsize = eventdatamodel1.size();
                                    if (datamodelsize != 0){
                                        TextView label1 = view.findViewById(R.id.layover2);
                                        label1.setVisibility(view.INVISIBLE);
                                    }


                                    PED_RecyclerViewAdapter adapter = new PED_RecyclerViewAdapter(getContext(), eventdatamodel1);
                                    recyclerView2.setAdapter(adapter);
                                    recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
                                    adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
        return view;
    }
}