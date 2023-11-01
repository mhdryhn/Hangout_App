package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final double EARTH_RADIUS = 6371;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Event Information");
    private CollectionReference notebookRef2 = db.collection("Tickets");
    private CollectionReference notebookRef3 = db.collection("Member Account Information");
    private DocumentReference noteRef = db.document("Event Information/New Event");
    private boolean isPageLoaded = false;

    //ArrayList for the Location recycler view
    ArrayList<event_data> eventdatamodel = new ArrayList<>();
    ArrayList<String> EventNameList = new ArrayList<>();
    ArrayList<String> EventDateList = new ArrayList<>();
    ArrayList<String> EventTimeList = new ArrayList<>();
    ArrayList<String> EventImagesList = new ArrayList<>();
    ArrayList<String> doc_idList = new ArrayList<>();

    //ArrayList for the Sports recycler view
    ArrayList<event_data> eventdatamodel1 = new ArrayList<>();
    ArrayList<String> EventNameList1 = new ArrayList<>();
    ArrayList<String> EventDateList1 = new ArrayList<>();
    ArrayList<String> EventTimeList1 = new ArrayList<>();
    ArrayList<String> EventImagesList1 = new ArrayList<>();
    ArrayList<String> doc_idList1 = new ArrayList<>();

    //ArrayList for the Educational recycler view
    ArrayList<event_data> eventdatamodel2 = new ArrayList<>();
    ArrayList<String> EventNameList2 = new ArrayList<>();
    ArrayList<String> EventDateList2 = new ArrayList<>();
    ArrayList<String> EventTimeList2 = new ArrayList<>();
    ArrayList<String> EventImagesList2 = new ArrayList<>();
    ArrayList<String> doc_idList2 = new ArrayList<>();


    //ArrayList for the Leisure recycler view
    ArrayList<event_data> eventdatamodel3 = new ArrayList<>();
    ArrayList<String> EventNameList3 = new ArrayList<>();
    ArrayList<String> EventDateList3 = new ArrayList<>();
    ArrayList<String> EventTimeList3 = new ArrayList<>();
    ArrayList<String> EventImagesList3 = new ArrayList<>();
    ArrayList<String> doc_idList3 = new ArrayList<>();

    ArrayList<String> TicketIDList = new ArrayList<>();
    ArrayList<String> TicketIDList1 = new ArrayList<>();
    ArrayList<String> TicketIDList2 = new ArrayList<>();
    ArrayList<String> TicketIDList3 = new ArrayList<>();

    GeoPoint user_location;

    //To get user email
    FirebaseUser user;
    FirebaseAuth auth;

    public home_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_fragment newInstance(String param1, String param2) {
        home_fragment fragment = new home_fragment();
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
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.nearbyeventrecyclerview);
        RecyclerView recyclerView2 = view.findViewById(R.id.sportseventrecyvlerview);
        RecyclerView recyclerView3 = view.findViewById(R.id.educationaleventrecyvlerview);
        RecyclerView recyclerView4 = view.findViewById(R.id.Leisureeventrecyvlerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
         notebookRef2.whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            notebookRef3.whereEqualTo("email", user.getEmail())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_member note = documentSnapshot.toObject(Note_member.class);
                                                user_location = note.getLocation();
                                            }
                                            notebookRef.whereGreaterThan("time_value", Calendar.getInstance().getTimeInMillis())
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                                double lat1 = Math.toRadians(user_location.getLatitude());
                                                                double lon1 = Math.toRadians(user_location.getLongitude());
                                                                GeoPoint event_location = note.getLocation();
                                                                double lat2 = Math.toRadians(event_location.getLatitude());
                                                                double lon2 = Math.toRadians(event_location.getLongitude());

                                                                double dLat = lat2 - lat1;
                                                                double dLon = lon2 - lon1;

                                                                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                                                        + Math.cos(lat1) * Math.cos(lat2)
                                                                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

                                                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                                                                double distance = EARTH_RADIUS * c;

                                                                if (distance < 10) {
                                                                    doc_idList.add(documentSnapshot.getId());
                                                                    EventNameList.add(note.getEvent_name());
                                                                    EventDateList.add(note.getDate());
                                                                    EventTimeList.add(note.getTime());
                                                                    EventImagesList.add(note.getImageurl());
                                                                }
                                                            }
                                                            for (int i = 0; i < EventNameList.size(); i++) {
                                                                eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventImagesList.get(i),  doc_idList.get(i)));
                                                            }

                                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel, layoutResourceID);
                                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                                            recyclerView.setAdapter(adapter);
                                                            recyclerView.setLayoutManager(layoutManager);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                        }
                                    });

                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member_ticket ticket = documentSnapshot.toObject(Note_member_ticket.class);
                                TicketIDList.add(ticket.getEvent_id());
                            }
                            notebookRef3.whereEqualTo("email", user.getEmail())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_member note = documentSnapshot.toObject(Note_member.class);
                                                user_location = note.getLocation();
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
                                                                Log.d("Event ID", "Event ID: " + event_id);

                                                                boolean isTicketID = false; // Flag variable
                                                                Log.d("TicketIDList Size", "Size: " + TicketIDList.size());
                                                                for (int i = 0; i < TicketIDList.size(); i++) {
                                                                    Log.d("Ticket ID", "Ticket ID at index " + i + ": " + TicketIDList.get(i));
                                                                    if (event_id.equals(TicketIDList.get(i))) {
                                                                        isTicketID = true; // Set flag to true
                                                                        break;
                                                                    }
                                                                }
                                                                Log.d("isTicketID", "Value: " + isTicketID);

                                                                if (!isTicketID) {
                                                                    double lat1 = Math.toRadians(user_location.getLatitude());
                                                                    double lon1 = Math.toRadians(user_location.getLongitude());
                                                                    GeoPoint event_location = note.getLocation();
                                                                    double lat2 = Math.toRadians(event_location.getLatitude());
                                                                    double lon2 = Math.toRadians(event_location.getLongitude());

                                                                    double dLat = lat2 - lat1;
                                                                    double dLon = lon2 - lon1;

                                                                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                                                                            + Math.cos(lat1) * Math.cos(lat2)
                                                                            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

                                                                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                                                                    double distance = EARTH_RADIUS * c;

                                                                    if (distance < 10) {
                                                                        doc_idList.add(documentSnapshot.getId());
                                                                        EventNameList.add(note.getEvent_name());
                                                                        EventDateList.add(note.getDate());
                                                                        EventTimeList.add(note.getTime());
                                                                        EventImagesList.add(note.getImageurl());
                                                                    }
                                                                }
                                                            }
                                                            for (int i = 0; i < EventNameList.size(); i++) {
                                                                eventdatamodel.add(new event_data(EventNameList.get(i), EventDateList.get(i), EventTimeList.get(i), EventImagesList.get(i), doc_idList.get(i)));
                                                            }

                                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel, layoutResourceID);
                                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                                            recyclerView.setAdapter(adapter);
                                                            recyclerView.setLayoutManager(layoutManager);
                                                            adapter.notifyDataSetChanged();

                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });

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
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                String category = note.getCategory();
                                                if(category.equals("Sports")) {
                                                    doc_idList1.add(documentSnapshot.getId());
                                                    EventNameList1.add(note.getEvent_name());
                                                    EventDateList1.add(note.getDate());
                                                    EventTimeList1.add(note.getTime());
                                                    EventImagesList1.add(note.getImageurl());
                                                }
                                            }
                                            for (int i = 0; i < EventNameList1.size(); i++) {
                                                eventdatamodel1.add(new event_data(EventNameList1.get(i), EventDateList1.get(i), EventTimeList1.get(i), EventImagesList1.get(i), doc_idList1.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel1, layoutResourceID);
                                            recyclerView2.setAdapter(adapter);
                                            recyclerView2.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member_ticket ticket = documentSnapshot.toObject(Note_member_ticket.class);
                                TicketIDList1.add(ticket.getEvent_id());
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
                                                Log.d("Event ID", "Event ID: " + event_id);

                                                boolean isTicketID = false; // Flag variable
                                                Log.d("TicketIDList Size", "Size: " + TicketIDList1.size());
                                                for (int i = 0; i < TicketIDList1.size(); i++) {
                                                    Log.d("Ticket ID", "Ticket ID at index " + i + ": " + TicketIDList1.get(i));
                                                    if (event_id.equals(TicketIDList1.get(i))) {
                                                        isTicketID = true; // Set flag to true
                                                        break;
                                                    }
                                                }
                                                Log.d("isTicketID", "Value: " + isTicketID);

                                                if (!isTicketID) {
                                                    if(note.getCategory().equals("Sports")) {
                                                        doc_idList1.add(documentSnapshot.getId());
                                                        EventNameList1.add(note.getEvent_name());
                                                        EventDateList1.add(note.getDate());
                                                        EventTimeList1.add(note.getTime());
                                                        EventImagesList1.add(note.getImageurl());
                                                    }
                                                }
                                            }
                                            for (int i = 0; i < EventNameList1.size(); i++) {
                                                eventdatamodel1.add(new event_data(EventNameList1.get(i), EventDateList1.get(i), EventTimeList1.get(i), EventImagesList1.get(i), doc_idList1.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel1, layoutResourceID);
                                            recyclerView2.setAdapter(adapter);
                                            recyclerView2.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    }
                    }
                });

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
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                String category = note.getCategory();
                                                if(category.equals("Educational")) {
                                                    doc_idList2.add(documentSnapshot.getId());
                                                    EventNameList2.add(note.getEvent_name());
                                                    EventDateList2.add(note.getDate());
                                                    EventTimeList2.add(note.getTime());
                                                    EventImagesList2.add(note.getImageurl());
                                                }
                                            }
                                            for (int i = 0; i < EventNameList2.size(); i++) {
                                                eventdatamodel2.add(new event_data(EventNameList2.get(i), EventDateList2.get(i), EventTimeList2.get(i), EventImagesList2.get(i), doc_idList2.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel2, layoutResourceID);
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            recyclerView3.setAdapter(adapter);
                                            recyclerView3.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member_ticket ticket = documentSnapshot.toObject(Note_member_ticket.class);
                                TicketIDList2.add(ticket.getEvent_id());
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
                                                Log.d("Event ID", "Event ID: " + event_id);

                                                boolean isTicketID = false; // Flag variable
                                                Log.d("TicketIDList Size", "Size: " + TicketIDList2.size());
                                                for (int i = 0; i < TicketIDList2.size(); i++) {
                                                    Log.d("Ticket ID", "Ticket ID at index " + i + ": " + TicketIDList2.get(i));
                                                    if (event_id.equals(TicketIDList2.get(i))) {
                                                        isTicketID = true; // Set flag to true
                                                        break;
                                                    }
                                                }
                                                Log.d("isTicketID", "Value: " + isTicketID);

                                                if (!isTicketID) {
                                                    if(note.getCategory().equals("Educational")) {
                                                        doc_idList2.add(documentSnapshot.getId());
                                                        EventNameList2.add(note.getEvent_name());
                                                        EventDateList2.add(note.getDate());
                                                        EventTimeList2.add(note.getTime());
                                                        EventImagesList2.add(note.getImageurl());
                                                    }
                                                }
                                            }
                                            for (int i = 0; i < EventNameList2.size(); i++) {
                                                eventdatamodel2.add(new event_data(EventNameList2.get(i), EventDateList2.get(i), EventTimeList2.get(i), EventImagesList2.get(i), doc_idList2.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel2, layoutResourceID);
                                            recyclerView3.setAdapter(adapter);
                                            recyclerView3.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });

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
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                String category = note.getCategory();
                                                if(category.equals("Leisure")) {
                                                    doc_idList3.add(documentSnapshot.getId());
                                                    EventNameList3.add(note.getEvent_name());
                                                    EventDateList3.add(note.getDate());
                                                    EventTimeList3.add(note.getTime());
                                                    EventImagesList3.add(note.getImageurl());
                                                }
                                            }
                                            for (int i = 0; i < EventNameList3.size(); i++) {
                                                eventdatamodel3.add(new event_data(EventNameList3.get(i), EventDateList3.get(i), EventTimeList3.get(i), EventImagesList3.get(i), doc_idList3.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel3, layoutResourceID);
                                            recyclerView4.setAdapter(adapter);
                                            recyclerView4.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_member_ticket ticket = documentSnapshot.toObject(Note_member_ticket.class);
                                TicketIDList3.add(ticket.getEvent_id());
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
                                                Log.d("Event ID", "Event ID: " + event_id);

                                                boolean isTicketID = false; // Flag variable
                                                Log.d("TicketIDList Size", "Size: " + TicketIDList3.size());
                                                for (int i = 0; i < TicketIDList3.size(); i++) {
                                                    Log.d("Ticket ID", "Ticket ID at index " + i + ": " + TicketIDList3.get(i));
                                                    if (event_id.equals(TicketIDList3.get(i))) {
                                                        isTicketID = true; // Set flag to true
                                                        break;
                                                    }
                                                }
                                                Log.d("isTicketID", "Value: " + isTicketID);

                                                if (!isTicketID) {
                                                    if(note.getCategory().equals("Leisure")) {
                                                        doc_idList3.add(documentSnapshot.getId());
                                                        EventNameList3.add(note.getEvent_name());
                                                        EventDateList3.add(note.getDate());
                                                        EventTimeList3.add(note.getTime());
                                                        EventImagesList3.add(note.getImageurl());
                                                    }
                                                }
                                            }
                                            for (int i = 0; i < EventNameList3.size(); i++) {
                                                eventdatamodel3.add(new event_data(EventNameList3.get(i), EventDateList3.get(i), EventTimeList3.get(i), EventImagesList3.get(i), doc_idList3.get(i)));
                                            }

                                            int layoutResourceID = R.layout.horizontal_recycler_view;
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                                            MED_RecyclerViewAdapter adapter = new MED_RecyclerViewAdapter(getContext(), eventdatamodel3, layoutResourceID);
                                            recyclerView4.setAdapter(adapter);
                                            recyclerView4.setLayoutManager(layoutManager);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
        return view;
    }
}
