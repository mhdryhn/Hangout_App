package com.example.fyp;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ED_RecyclerViewAdapter extends RecyclerView.Adapter<ED_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<event_data> eventdatamodels;

    String selectedEventId;

    public ED_RecyclerViewAdapter(Context context, ArrayList<event_data> eventdatamodels, String selectedEventID){
        this.context = context;
        this.eventdatamodels = eventdatamodels;
        this.selectedEventId = selectedEventID;
    }

    public void updateData(ArrayList<event_data> newData) {
        eventdatamodels.clear();
        eventdatamodels.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ED_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.event_recyclerview_row, parent, false);
            return new ED_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ED_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.ev_name.setText(eventdatamodels.get(position).getEvent_name());
        holder.ev_time.setText(eventdatamodels.get(position).getTime());
        holder.ev_date.setText(eventdatamodels.get(position).getDate());
        holder.button1.setText(eventdatamodels.get(position).getMembers());
        Picasso.get().load(eventdatamodels.get(position).getImage_url()).into(holder.imageView);
        holder.doc_id.setText(eventdatamodels.get(position).getDoc_id());
    }

    @Override
    public int getItemCount() {
        //Number of items you want displayed
        return eventdatamodels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView ev_name;
        TextView ev_time;
        TextView ev_date, doc_id;

        Button button1, buttoninfo;

        Dialog myDialog;

        Button button;
        ImageView imageView;

        ArrayList<String> MemberNameList = new ArrayList<>();
        ArrayList<String> MemberNumberList = new ArrayList<>();
        ArrayList<Note_member_ticket> memberdatamodel = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notebookRef = db.collection("Event Information");
        CollectionReference notebookRef2 = db.collection("Tickets");

        public MyViewHolder(@NonNull View itemView) {
            //grabbing the views from our recycler view row layout file
            //Kinda Like an oncreate method
            super(itemView);

            ev_name = itemView.findViewById(R.id.eventname);
            ev_time = itemView.findViewById(R.id.eventtime);
            ev_date = itemView.findViewById(R.id.eventdate);
            button = itemView.findViewById(R.id.delete);
            button1 = itemView.findViewById(R.id.Num_members);
            buttoninfo = itemView.findViewById(R.id.popup);
            myDialog = new Dialog(itemView.getContext());
            imageView = itemView.findViewById(R.id.eventimage);
            doc_id = itemView.findViewById(R.id.docid);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notebookRef.document(doc_id.getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    documentSnapshot.getReference().delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(itemView.getContext(), "Event Deleted", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(itemView.getContext(), "Event Not Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(itemView.getContext(), "Event Not Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            buttoninfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth auth;
                    FirebaseUser user;

                    myDialog.setContentView(R.layout.event_members_popup);
                    auth = FirebaseAuth.getInstance();

                    TextView label2 = myDialog.findViewById(R.id.label2);
                    TextView label3 = myDialog.findViewById(R.id.contactNumber);

                    label2.setVisibility(View.INVISIBLE);
                    label3.setVisibility(View.INVISIBLE);



                    RecyclerView members = myDialog.findViewById(R.id.Members_RecyclerView);
                    MemberNameList.clear();
                    MemberNumberList.clear();
                    memberdatamodel.clear();

                    notebookRef2.whereEqualTo("event_id", doc_id.getText())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Note_member_ticket note = documentSnapshot.toObject(Note_member_ticket.class);
                                        {
                                            String number = String.valueOf(note.getNumber());
                                            MemberNameList.add(note.getName());
                                            MemberNumberList.add(number);
                                        }
                                    }
                                        for (int i = 0; i < MemberNameList.size(); i++) {
                                            memberdatamodel.add(new Note_member_ticket(MemberNameList.get(i), MemberNumberList.get(i), i+1));
                                        }

                                        // Update the RecyclerView adapter with the new event data
                                        MemberList_RecyclerViewAdapter adapter = new MemberList_RecyclerViewAdapter(myDialog.getContext(), memberdatamodel);
                                        members.setAdapter(adapter);
                                        members.setLayoutManager(new LinearLayoutManager(myDialog.getContext()));

                                    int datamodelsize = memberdatamodel.size();
                                    if (datamodelsize != 0){
                                        TextView label1 = myDialog.findViewById(R.id.noreviews);
                                        label1.setVisibility(View.INVISIBLE);
                                        label2.setVisibility(View.VISIBLE);
                                        label3.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                    myDialog.show();
                 }
            });
        }
    }
}
