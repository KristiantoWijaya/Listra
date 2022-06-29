package com.example.listra.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listra.AddTask;
import com.example.listra.MainActivity;
import com.example.listra.R;
import com.example.listra.model.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    private List<TaskModel> tasklist;
    private MainActivity activity;
    private FirebaseFirestore firestore;
    public static final String[] MONTHS = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};

    public TaskAdapter(MainActivity mainActivity, List<TaskModel> tasklist){
        this.tasklist=tasklist;
        activity = mainActivity;
    }
    public Context getContext(){
        return activity;
    }
//method to delete the task
    public void deleteTask (int position){
        TaskModel toDoModel = tasklist.get(position);
        //remove the data from firebase
        firestore.collection("Task").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("list")
                .document(toDoModel.TaskId).delete();
        //remove task from recyclerview
        tasklist.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        //check if the tasklist is now empty
        activity.checkView();
    }
//method for editing a task
    public void editTask (int position){
        TaskModel toDoModel = tasklist.get(position);
        Bundle bundle = new Bundle();
        //put the old task in the textview
        bundle.putString("Task", toDoModel.getTask());
        //if current dueDate is null display null, else display the date
        if(toDoModel.getDue()==null){
            bundle.putString("Due", null);
        }
        else{
            bundle.putString("Due", toDoModel.getDue());
        }
        bundle.putString("id" , toDoModel.TaskId);
        bundle.putInt("time", toDoModel.getTime());
        //call the Add Task class to update the task
        AddTask addNewTask = new AddTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the task_card activity layout on create
        View view = LayoutInflater.from(activity).inflate(R.layout.task_card, parent, false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskModel taskModel = tasklist.get(position);
        holder.mCheckBox.setText(taskModel.getTask());
        holder.mCheckBox.setChecked(toBool(taskModel.getStatus()));
        holder.mDueDate.setText(taskModel.getDue());
        holder.mStatusDate.setText(" ");
        //check if the due date is null or not
        if (taskModel.getDue()!=null){
            //if due date is not null, display the textview else remove the textview
            holder.mDueDate.setVisibility(View.VISIBLE);
        }
        else {
            holder.mDueDate.setVisibility(View.GONE);
        }
        setStatus(holder, position);
        //check if checkbox is checked upon start
        if (holder.mCheckBox.isChecked()){
            holder.mStatusDate.setVisibility(View.VISIBLE);
            //change the colour and status of the date
            holder.mStatusDate.setText("Completed");
            //change status colour
            holder.mStatusColour.setBackgroundResource(R.drawable.status_completed);
            //add a strikethrough effect to the textview
            holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            //remove strikethrough
            setStatus(holder, position);
            holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

        }

        //check if checkbox's status is changed
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.mCheckBox.isChecked()){
                    holder.mStatusDate.setVisibility(View.VISIBLE);
                    //change status colour
                    holder.mStatusDate.setText("Completed");
                    holder.mStatusColour.setBackgroundResource(R.drawable.status_completed);
                    //add a strikethrough effect to the textview
                    holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    //update the checkbox's status in firebase
                    firestore.collection("Task").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("list").document(taskModel.TaskId).update("Status", 1);
                }
                else {
                    //change status colour
                   setStatus(holder, position);
                    //remove strikethrough
                    holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    //update checkbox's status in firebase
                    firestore.collection("Task").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("list").document(taskModel.TaskId).update("Status", 0);
                }
            }
        });

    }

    private boolean toBool(int stat){
        return stat !=0;
    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }

    public void setStatus (@NonNull MyViewHolder holder, int position){
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        String curDate = ""+curDay + " " + MONTHS[curMonth] + " " + curYear;
        String taskDate = holder.mDueDate.getText().toString().trim();
        try {
            holder.mStatusColour.setBackgroundResource(R.drawable.status_upcoming);
            holder.mStatusDate.setVisibility(View.GONE);
            Date date1 = format.parse(curDate);
            Date date2 = format.parse(taskDate);
            Boolean bool1 = date1.after(date2);
            Boolean bool2 = date1.before(date2);
            Boolean bool3 = date1.equals(date2);
            if (bool1){
                holder.mStatusColour.setBackgroundResource(R.drawable.status_colour);

                holder.mStatusDate.setVisibility(View.VISIBLE);
                holder.mStatusDate.setText("Overdue");
            }
            else if (bool2){
                holder.mStatusColour.setBackgroundResource(R.drawable.status_upcoming);
                holder.mStatusDate.setVisibility(View.VISIBLE);
                holder.mStatusDate.setText("Upcoming");
            }else if (bool3){
                holder.mStatusColour.setBackgroundResource(R.drawable.status_today);
                holder.mStatusDate.setVisibility(View.VISIBLE);
                holder.mStatusDate.setText("Today");
            }
            else{
                holder.mStatusColour.setBackgroundResource(R.drawable.status_upcoming);
                holder.mStatusDate.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setTask (List<TaskModel> todolist){
        this.tasklist = todolist;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mDueDate, mStatusDate, mStatusColour;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mStatusDate = itemView.findViewById(R.id.statusDate);
            mDueDate = itemView.findViewById(R.id.date_card);
            mCheckBox = itemView.findViewById(R.id.checkBox);
            mStatusColour = itemView.findViewById(R.id.statusColour);
        }
    }
}
