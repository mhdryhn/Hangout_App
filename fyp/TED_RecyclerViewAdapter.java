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

public class TED_RecyclerViewAdapter extends RecyclerView.Adapter<TED_RecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<event_data> eventdatamodels;


    public TED_RecyclerViewAdapter(Context context, ArrayList<event_data> eventdatamodels) {
        this.context = context;
        this.eventdatamodels = eventdatamodels;
    }

    @NonNull
    @Override
    public TED_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_memberevent_recyclerview_row, parent, false);
        return new TED_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TED_RecyclerViewAdapter.MyViewHolder holder, int position) {
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

        ImageView imageView;
        Button button;

        Dialog myDialog;

        TextView name, event_name, location;


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


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference notebookRef = db.collection("Event Information");
            CollectionReference notebookRef3 = db.collection("Tickets");




            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView close;
                    Button joinevent;
                    FirebaseAuth auth;
                    FirebaseUser user;

                    myDialog.setContentView(R.layout.ticket_popup);
                    auth = FirebaseAuth.getInstance();
                    user = auth.getCurrentUser();

                    name = myDialog.findViewById(R.id.TicketHolder_Name);
                    event_name = myDialog.findViewById(R.id.Event_name);

                    event_name.setText(ev_name.getText());


                    notebookRef3.whereEqualTo("email", user.getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        Note_member_ticket note = documentSnapshot.toObject(Note_member_ticket.class);
                                        name.setText(note.getName());
                                        break;
                                    }
                                }
                            });;
                    myDialog.show();
                }
            });

        }
    }
}

