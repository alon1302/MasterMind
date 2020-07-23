package com.example.mastermind.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mastermind.R;
import com.example.mastermind.model.game.*;
import com.example.mastermind.model.listeners.OnPegClickListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRows extends RecyclerView.Adapter<AdapterRows.ViewHolder> {
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;
    private Context context;
    public LinearLayout fullRow;

    public AdapterRows(ArrayList<GameRow> gameRows, ArrayList<CheckRow> checkRows, Context context, RecyclerView recyclerView) {
        this.gameRows = gameRows;
        this.checkRows = checkRows;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameRow currGameRow = gameRows.get(position);
        CheckRow currCheckRow = checkRows.get(position);
        String[] colorCheckRow = currCheckRow.getStringRow();
        //Log.d("TAG", "colorCheckRow: " + Arrays.toString(colorCheckRow));
        String[] colorGameRow = currGameRow.getStringRow();
        //Log.d("TAG", "colorGameRow: " + Arrays.toString(colorGameRow));
        for (int i = 0; i < holder.SIZE; i++) {
            final int finalI = i;
            holder.game[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnPegClickListener pegClickListener = (OnPegClickListener) context;
                    pegClickListener.onPositionClicked(finalI);
                }
            });
            switch (colorGameRow[i]) {
                case "null":
                    holder.game[i].setImageResource(R.color.colorTWhite);
                    break;
                case "red":
                    holder.game[i].setImageResource(R.color.colorRed);
                    break;
                case "green":
                    holder.game[i].setImageResource(R.color.colorGreen);
                    break;
                case "blue":
                    holder.game[i].setImageResource(R.color.colorBlue);
                    break;
                case "orange":
                    holder.game[i].setImageResource(R.color.colorOrange);
                    break;
                case "yellow":
                    holder.game[i].setImageResource(R.color.colorYellow);
                    break;
                case "light":
                    holder.game[i].setImageResource(R.color.colorLight);
                    break;
            }
        }
        colorCheckRow = sortCheckRow(colorCheckRow);
        for (int i = 0; i < holder.SIZE; i++) {
            switch (colorCheckRow[i]) {
                case "null":
                    holder.check[i].setVisibility(View.INVISIBLE);
                    break;
                case "black":
                    holder.check[i].setVisibility(View.VISIBLE);
                    holder.check[i].setImageResource(R.color.colorBlack);
                    break;
                case "white":
                    holder.check[i].setVisibility(View.VISIBLE);
                    holder.check[i].setImageResource(R.color.colorWhite);
                    break;
            }
        }
    }

    public String[] sortCheckRow(String[] arr) {
        String[] newArr = new String[4];
        int black = 0, white = 0;
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = "null";
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals("black")) {
                black++;
            } else if (arr[i].equals("white")) {
                white++;
            }
        }
        for (int i = 0; i < black; i++) {
            newArr[i] = "black";
        }
        for (int i = black; i < black + white; i++) {
            newArr[i] = "white";
        }
        return newArr;
    }

    @Override
    public int getItemCount() {
        return gameRows.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        int SIZE = 4;
        public CircleImageView[] game;
        public CircleImageView[] check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            game = new CircleImageView[SIZE];
            game[0] = itemView.findViewById(R.id.game_0);
            game[1] = itemView.findViewById(R.id.game_1);
            game[2] = itemView.findViewById(R.id.game_2);
            game[3] = itemView.findViewById(R.id.game_3);
            check = new CircleImageView[SIZE];
            check[0] = itemView.findViewById(R.id.check_0);
            check[1] = itemView.findViewById(R.id.check_1);
            check[2] = itemView.findViewById(R.id.check_2);
            check[3] = itemView.findViewById(R.id.check_3);
            // reference to linear layout
            fullRow = itemView.findViewById(R.id.fullRow);
        }
    }
}
