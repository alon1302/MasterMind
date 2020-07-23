package com.example.mastermind.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mastermind.R;
import com.example.mastermind.model.game.Record;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecords extends RecyclerView.Adapter<AdapterRecords.ViewHolder> {
    private ArrayList<Record> records;
    private Context context;
    public LinearLayout record;

    public AdapterRecords(ArrayList<Record> records,Context context, RecyclerView recyclerView) {
        //isdyfgosmadi
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecords.ViewHolder holder, int position) {
        Record currRecord = records.get(position);
        holder.img.setImageURI(currRecord.getImgUri());
        holder.name.setText(currRecord.getName());
        long minutes = (currRecord.getTime() / 1000) / 60;
        long seconds = (currRecord.getTime() / 1000) % 60;
        holder.time.setText(String.format(Locale.getDefault(),"%02d:%02d",minutes, seconds));
        holder.sn.setText(position);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView img;
        public TextView name;
        public TextView time;
        public TextView sn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            sn = itemView.findViewById(R.id.sn);
            // reference to linear layout
            record = itemView.findViewById(R.id.record);
        }
    }
}
