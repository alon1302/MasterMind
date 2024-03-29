package com.example.mastermind.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mastermind.R;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.theme.Theme;
import com.example.mastermind.model.theme.Themes;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterThemes extends RecyclerView.Adapter<AdapterThemes.ThemeViewHolder> {
    private final ArrayList<Theme> list;
    private final Context context;

    public AdapterThemes(Context context) {
        this.context = context;
        this.list = Themes.getInstance(context.getApplicationContext()).getAllThemes();
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_theme, parent, false);
        return new ThemeViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final ThemeViewHolder holder, final int position) {
        for (int i = 0; i < holder.imageViews.length; i++)
            holder.imageViews[i].setForeground(context.getResources().getDrawable(list.get(position).getPegImage()));
        if (list.get(position).isOpened())
            holder.status.setImageResource(R.drawable.ic_baseline_lock_open_24);
        else
            holder.status.setImageResource(R.drawable.ic_baseline_lock_24);
        if (position == Themes.getInstance(context.getApplicationContext()).getCurrentThemeIndex())
            holder.status.setImageResource(R.drawable.ic_baseline_check_24);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MethodCallBack methodCallBack = (MethodCallBack) context;
                methodCallBack.onCallBack(position, holder.imageViews[0].getForeground());
            }
        });
    }

    @Override
    public int getItemCount() {
        return Themes.getInstance(context.getApplicationContext()).getAllThemes().size();
    }

    public static class ThemeViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView[] imageViews;
        public ImageView status;

        public ThemeViewHolder(View view) {
            super(view);
            imageViews = new CircleImageView[6];
            imageViews[0] = view.findViewById(R.id.red);
            imageViews[1] = view.findViewById(R.id.green);
            imageViews[2] = view.findViewById(R.id.blue);
            imageViews[3] = view.findViewById(R.id.orange);
            imageViews[4] = view.findViewById(R.id.yellow);
            imageViews[5] = view.findViewById(R.id.light);
            status = view.findViewById(R.id.imageView_status);
        }
    }
}
