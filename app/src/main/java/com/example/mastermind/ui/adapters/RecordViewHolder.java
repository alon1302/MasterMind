package com.example.mastermind.ui.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mastermind.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView img;
    public TextView name;
    public TextView time;
    public TextView sn;

    public RecordViewHolder(@NonNull View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.img);
        name = itemView.findViewById(R.id.name);
        time = itemView.findViewById(R.id.time);
        sn = itemView.findViewById(R.id.sn);
    }
}
