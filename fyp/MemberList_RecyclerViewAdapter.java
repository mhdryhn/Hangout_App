package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MemberList_RecyclerViewAdapter extends RecyclerView.Adapter<MemberList_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Note_member_ticket> memberdatamodel;

    public MemberList_RecyclerViewAdapter(Context context, ArrayList <Note_member_ticket> memberdatamodel){
        this.context = context;
        this.memberdatamodel = memberdatamodel;
    }

    public void updateData(ArrayList<Note_member_ticket> newData) {
        memberdatamodel.clear();
        memberdatamodel.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberList_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_info_row, parent, false);
        return new MemberList_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberList_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.user_name.setText(memberdatamodel.get(position).getName());
        holder.user_number.setText(memberdatamodel.get(position).getNumber_string());
        holder.s_no.setText("" + memberdatamodel.get(position).getS_no()+")");
    }

    @Override
    public int getItemCount() {
        //Number of items you want displayed
        return memberdatamodel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView user_name, user_number, s_no;

        public MyViewHolder(@NonNull View itemView) {
            //grabbing the views from our recycler view row layout file
            //Kinda Like an oncreate method
            super(itemView);

            user_number = itemView.findViewById(R.id.contactNumber);
            user_name = itemView.findViewById(R.id.userName);
            s_no = itemView.findViewById(R.id.Serial_no);
        }
    }
}

