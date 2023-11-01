package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class RED_RecyclerViewAdapter extends RecyclerView.Adapter<RED_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Note_review> reviewsdatamodel;

    String item_id;

    public RED_RecyclerViewAdapter(Context context, ArrayList <Note_review> reviewsdatamodel){
        this.context = context;
        this.reviewsdatamodel = reviewsdatamodel;
    }

    public void updateData(ArrayList<Note_review> newData) {
        reviewsdatamodel.clear();
        reviewsdatamodel.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RED_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_review_row, parent, false);
        return new RED_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RED_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.rev_name.setText(reviewsdatamodel.get(position).getUser_name());
        holder.rev_content.setText(reviewsdatamodel.get(position).getReview_content());
        holder.rev_value.setText(String.valueOf(reviewsdatamodel.get(position).getEvent_rating()));
        holder.rev_id.setText(String.valueOf(reviewsdatamodel.get(position).getEvent_id()));
    }

    @Override
    public int getItemCount() {
        //Number of items you want displayed
        return reviewsdatamodel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rev_name;
        TextView rev_value;
        TextView rev_content;

        TextView rev_info;

        TextView rev_id;

        String event_id;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notebookRef = db.collection("Event Information");


        public MyViewHolder(@NonNull View itemView) {
            //grabbing the views from our recycler view row layout file
            //Kinda Like an oncreate method
            super(itemView);

            rev_name = itemView.findViewById(R.id.reviewer_name);
            rev_value = itemView.findViewById(R.id.rating);
            rev_content = itemView.findViewById(R.id.Review_Text);
            rev_id = itemView.findViewById(R.id.event_id);
            rev_info = itemView.findViewById(R.id.EventDateandname);



            notebookRef
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note_Event note = documentSnapshot.toObject(Note_Event.class);
                                note.setDocumentId(documentSnapshot.getId());
                                event_id = note.getDocumentId();
                                if(event_id.equals(rev_id.getText())){
                                    String event_name = note.getEvent_name();
                                    String event_date = note.getDate();
                                    rev_info.setText(event_name + "-" + event_date);
                                }
                            }
                        }
                    });

        }
    }
}
