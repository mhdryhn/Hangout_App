package com.example.fyp;

import static androidx.core.content.ContextCompat.createDeviceProtectedStorageContext;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MED_RecyclerViewAdapter extends RecyclerView.Adapter<MED_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<event_data> eventdatamodels;
    int layoutResourceID;


    public MED_RecyclerViewAdapter(Context context, ArrayList<event_data> eventdatamodels, int layoutResourceID) {
        this.context = context;
        this.eventdatamodels = eventdatamodels;
        this.layoutResourceID = layoutResourceID;
    }

    @NonNull
    @Override
    public MED_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == 1) {
            view = inflater.inflate(R.layout.activity_memberevent_recyclerview_row, parent, false);
        } else {
            view = inflater.inflate(R.layout.horizontal_recycler_view, parent, false);
        }
        return new MED_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MED_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.ev_name.setText(eventdatamodels.get(position).getEvent_name());
        holder.ev_time.setText(eventdatamodels.get(position).getTime());
        holder.ev_date.setText(eventdatamodels.get(position).getDate());
        holder.doc_id.setText(eventdatamodels.get(position).getDoc_id());
        Picasso.get().load(eventdatamodels.get(position).getImage_url()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        //Number of items you want displayed
        return eventdatamodels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (layoutResourceID == R.layout.activity_memberevent_recyclerview_row) {
            return 1;
        } else {
            return 2;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ev_name;
        TextView ev_time;
        TextView ev_date;

        Button button;

        Dialog myDialog;

        ImageView imageView;

        TextView category,time, date, location, additional_info, doc_id;

        int max_members;

        public MyViewHolder(@NonNull View itemView) {
            //grabbing the views from our recycler view row layout file
            //Kinda Like an oncreate method
            super(itemView);
            button = itemView.findViewById(R.id.more_infobutton);
            myDialog = new Dialog(itemView.getContext());

            ev_name = itemView.findViewById(R.id.eventname);
            ev_time = itemView.findViewById(R.id.eventtime);
            ev_date = itemView.findViewById(R.id.eventdate);
            imageView = itemView.findViewById(R.id.eventimage);
            doc_id = itemView.findViewById(R.id.docid);


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference notebookRef = db.collection("Event Information");
            CollectionReference notebookRef3 = db.collection("Tickets");
            DocumentReference noteRef = db.document("Event Information/New Event");
            CollectionReference notebookRef2 = db.collection("Member Account Information");



            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView close;
                    Button joinevent;
                    FirebaseAuth auth;
                    FirebaseUser user;

                    myDialog.setContentView(R.layout.activity_eventdetails_pop_up);
                    close = myDialog.findViewById(R.id.cross);
                    joinevent = myDialog.findViewById(R.id.btnjoinevent);
                    auth = FirebaseAuth.getInstance();
                    user = auth.getCurrentUser();

                    category = myDialog.findViewById(R.id.Event_Category_Tb);
                    time = myDialog.findViewById(R.id.Event_Time_tb);
                    date = myDialog.findViewById(R.id.Event_Date_tb);
                    location = myDialog.findViewById(R.id.Event_Location_tb);
                    additional_info = myDialog.findViewById(R.id.Additional_Info_tb);


                    notebookRef.document(doc_id.getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                    time.setText(note.getTime());
                                    date.setText(note.getDate());
                                    additional_info.setText(note.getAdditional_info());
                                    category.setText(note.getCategory());
                                    try {
                                        GeoPoint eventlocation = note.getLocation();
                                        Geocoder geocoder = new Geocoder(myDialog.getContext(), Locale.getDefault());
                                        List<Address> addresses = geocoder.getFromLocation(eventlocation.getLatitude(), eventlocation.getLongitude(), 1);
                                        String address = addresses.get(0).getAddressLine(0);
                                        location.setText(address);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    note.setDocumentId(documentSnapshot.getId());
                                    max_members = note.getMax_members();
                                }
                            });

                    joinevent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notebookRef.document(doc_id.getText().toString())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                    if(note.getCurrent_members() < note.getMax_members()) {
                                                        Log.d("Before:", "Members" + note.getCurrent_members());
                                                        int members = note.getCurrent_members();
                                                        members = members + 1;
                                                        documentSnapshot.getReference().update("current_members", members)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Document updated successfully
                                                                        Log.d("Firestore", "Document updated successfully");
                                                                    }
                                                                });
                                                    }
                                                    else{
                                                        Toast.makeText(myDialog.getContext(), "Sorry this event has reached it capacity for attendees", Toast.LENGTH_SHORT).show();
                                                        myDialog.dismiss();
                                                        return;
                                                    }
                                                }
                                            });

                            notebookRef2.whereEqualTo("email",  user.getEmail())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Note_member note = documentSnapshot.toObject(Note_member.class);
                                            Note_member_ticket new_note = new Note_member_ticket(note.getName(), note.getNumber(), note.getEmail(), note.getGender(), doc_id.getText().toString());
                                            notebookRef3.add(new_note)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(myDialog.getContext(), "Event Joined", Toast.LENGTH_SHORT).show();
                                                            myDialog.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(myDialog.getContext(), "Error!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                    });
                                            }
                                        }
                                    });
                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                    Log.d("Button Click", "more_infobutton clicked");
                }
            });

        }
    }
}
