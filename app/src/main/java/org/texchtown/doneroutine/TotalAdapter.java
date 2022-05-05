package org.texchtown.doneroutine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TotalAdapter extends RecyclerView.Adapter<TotalAdapter.TotalViewHolder> {
    //TAG
    private static final String TAG = "TotalAdapter";
    //var
    Context context;
    private ArrayList<String> group_name, group_detail, group_percentage;
    private ArrayList<Integer> group_color;

    public TotalAdapter(Context context, ArrayList<String> group_name, ArrayList<String> group_detail
            , ArrayList<String> group_percentage, ArrayList<Integer> group_color) {
        this.context = context;
        this.group_name = group_name;
        this.group_detail = group_detail;
        this.group_percentage = group_percentage;
        this.group_color = group_color;
    }

    @NonNull
    @Override
    public TotalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_total, parent, false);
        return new TotalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TotalViewHolder holder, int position) {
        holder.tv_group_name.setText(group_name.get(position));
        holder.tv_habits.setText(group_detail.get(position));;
        holder.tv_percentage.setText(group_percentage.get(position));;
        holder.cardView_bg.setBackgroundColor(group_color.get(position));
        holder.cardView_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) context;
            }
        });
    }

    @Override
    public int getItemCount() {
        return group_name.size();
    }

    public class TotalViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardView_bg;
        TextView tv_group_name, tv_habits, tv_percentage;

        public TotalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView_bg = itemView.findViewById(R.id.group_layout);
            tv_group_name = itemView.findViewById(R.id.group_name);
            tv_habits = itemView.findViewById(R.id.group_habits);
            tv_percentage = itemView.findViewById(R.id.group_percentage);
        }
    }
}
