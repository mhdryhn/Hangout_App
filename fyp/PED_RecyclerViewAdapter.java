package com.example.fyp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PED_RecyclerViewAdapter extends RecyclerView.Adapter<PED_RecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<event_data> eventdatamodels;


    public PED_RecyclerViewAdapter(Context context, ArrayList<event_data> eventdatamodels) {
        this.context = context;
        this.eventdatamodels = eventdatamodels;
    }

    @NonNull
    @Override
    public PED_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.memberpastevent_recyclerview_row, parent, false);
        return new PED_RecyclerViewAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PED_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.ev_name.setText(eventdatamodels.get(position).getEvent_name());
        holder.ev_time.setText(eventdatamodels.get(position).getTime());
        holder.ev_date.setText(eventdatamodels.get(position).getDate());
        Picasso.get().load(eventdatamodels.get(position).getImage_url()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        //Number of items you want displayed
        return eventdatamodels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ev_name;
        TextView ev_time;
        TextView ev_date;

        EditText rv_content;

        ImageView imageView;
        Button button, review_button;

        Dialog myDialog;

        String member_name, event_id, review_info, organizer_email;

        float rating_value;

        RatingBar event_rating;

        public MyViewHolder(@NonNull View itemView) {
            //grabbing the views from our recycler view row layout file
            //Kinda Like an oncreate method
            super(itemView);

            myDialog = new Dialog(itemView.getContext());
            button = itemView.findViewById(R.id.Review_open);
            ev_name = itemView.findViewById(R.id.eventname);
            ev_time = itemView.findViewById(R.id.eventtime);
            ev_date = itemView.findViewById(R.id.eventdate);
            imageView = itemView.findViewById(R.id.eventimage);


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference notebookRef = db.collection("Event Information");
            CollectionReference notebookRef2 = db.collection("Member Account Information");
            CollectionReference notebookRef3 = db.collection("Review");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.setContentView(R.layout.review_popup);
                    myDialog.show();
                    review_button = myDialog.findViewById(R.id.submit_review);
                    rv_content = myDialog.findViewById(R.id.review_content);
                    event_rating = myDialog.findViewById(R.id.event_rating);
                    event_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            rating_value = ratingBar.getRating();
                        }
                    });

                    review_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth auth;
                            FirebaseUser user;
                            auth = FirebaseAuth.getInstance();
                            user = auth.getCurrentUser();

                            review_info = String.valueOf(rv_content.getText());
                            notebookRef2.whereEqualTo("email", user.getEmail())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Note_member note3 = documentSnapshot.toObject(Note_member.class);
                                                member_name= note3.getName();
                                                notebookRef.whereEqualTo("event_name", ev_name.getText())
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                                    Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                                                    note.setDocumentId(documentSnapshot.getId());
                                                                    event_id = note.getDocumentId();
                                                                    organizer_email = note.getOrganizer_email();
                                                                    Note_review note1 = new Note_review(review_info, rating_value, event_id, member_name, organizer_email);
                                                                    notebookRef3.add(note1)
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentReference documentReference) {
                                                                                    Toast.makeText(myDialog.getContext(), "Review has been sent to the organizer", Toast.LENGTH_SHORT).show();
                                                                                    myDialog.dismiss();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(myDialog.getContext(), "Error!!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    });
                }
            });

        }
    }
}
