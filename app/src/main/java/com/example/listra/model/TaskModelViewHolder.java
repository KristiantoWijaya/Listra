package com.example.listra.model;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listra.R;

public class TaskModelViewHolder extends RecyclerView.ViewHolder {
    public TextView date;
    public CheckBox task;

    public TaskModelViewHolder(@NonNull View itemView) {
        super(itemView);
        task = itemView.findViewById(R.id.checkBox);
        date = itemView.findViewById(R.id.date_card);

    }
}
