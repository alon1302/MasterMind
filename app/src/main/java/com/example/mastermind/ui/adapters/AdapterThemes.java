package com.example.mastermind.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mastermind.R;
import com.example.mastermind.model.Theme;
import com.example.mastermind.model.Themes;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterThemes extends RecyclerView.Adapter<AdapterThemes.ThemesViewHolder> {
    Context context;

    public AdapterThemes(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ThemesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_theme, parent, false);
        return new ThemesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThemesViewHolder holder, final int position) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("ThemesPrefs:" + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
        ArrayList<Theme> list = Themes.getInstance(context.getApplicationContext()).getAllThemes();
        for (int i = 0; i < holder.imageViews.length; i++) {
            holder.imageViews[i].setForeground(context.getResources().getDrawable(list.get(position).getPegImage()));
        }
        if (sharedPreferences.getBoolean("" + position, false)){
            holder.status.setImageResource(R.drawable.ic_baseline_lock_open_24);
            if (position == sharedPreferences.getInt("index",0)){
                holder.status.setImageResource(R.drawable.ic_baseline_check_24);
            }
        }
        else {
            holder.status.setImageResource(R.drawable.ic_baseline_lock_24);
            if (position == sharedPreferences.getInt("index",0)){
                holder.status.setImageResource(R.drawable.ic_baseline_check_24);
            }
        }

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

    public class ThemesViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView[] imageViews;
        public ImageView status;

        public ThemesViewHolder(View view) {
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
