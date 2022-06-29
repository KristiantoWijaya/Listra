package com.example.listra;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddTask";
    private TextView setDueDate;
    private EditText mTaskEdit;
    private Button mSaveBtn, mCancelButton;
    private FirebaseFirestore firestore;
    private Context context;
    private String dueDate;
    private int dateINT;
    private String id = "";
    private  String dueDateUpdate = "";
    private int timeDateUpdate = 0;
    private ProgressBar progressBar;
    public static final String[] MONTHS = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};

    public static AddTask newInstance() {
        return new AddTask();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_addtask, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setDueDate = view.findViewById(R.id.set_due_tv);
        mTaskEdit = view.findViewById(R.id.task_edittext);
        mSaveBtn = view.findViewById(R.id.savebutton);
        mCancelButton = view.findViewById(R.id.cancelbutton);
        firestore = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        //check if user is updating the task
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            //add previous data if the task is updated
            String task = bundle.getString("Task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("Due");
            timeDateUpdate = bundle.getInt("time");
            mTaskEdit.setText(task);

            //if dueDate is not null set the date to the old date, else put in placeholder text
            if(dueDateUpdate!=null){
                setDueDate.setText(dueDateUpdate);
            }
            else setDueDate.setText("Set Due Date");

        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adding a calendar date picker for user to choose the dates easily
                Calendar calendar = Calendar.getInstance();
                int MM = calendar.get(Calendar.MONTH);
                int YYYY = calendar.get(Calendar.YEAR);
                int DD = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        // setting the setduedate text in the add task page to the date picked
                        setDueDate.setText(dayOfMonth + " " + MONTHS[month] + " " + year);
                        dueDate = ""+dayOfMonth + " " + MONTHS[month] + " " + year;
                        month+=1;
                        dateINT = Integer.valueOf("" + year + month + dayOfMonth);
                    }


                },YYYY,MM,DD);
                datePickerDialog.show();
            }
        });

        boolean finalIsUpdate = isUpdate;
        //dismiss the page if user pressed the cancel button
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               dismiss();
            }
        });
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String task = mTaskEdit.getText().toString();
                //if user is updating the task
                if (finalIsUpdate){
                    if(dueDate == null){
                        //if the user didnt add a date, the previous date will be displayed
                        dueDate = dueDateUpdate;
                        dateINT = timeDateUpdate;
                    }
                    //adding the data that user has inputted to firestore
                    firestore.collection("Task").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("list").document(id).update("Task", task, "Due", dueDate, "time", dateINT);
                    //removing the loading bar and notify the user that task has been added
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                    dismiss();

                }
                //if user is adding the task
                else {
                    //notify hte user if the task field is empty
                     if(task.isEmpty()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Task Is Empty!", Toast.LENGTH_SHORT).show();
                      }
                    else{
                        //putting the data into an array
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("Task", task);
                        taskMap.put("Due", dueDate);
                        taskMap.put("Status", 0);
                        taskMap.put("time", dateINT);
                        //adding the array with the data into firestore
                        firestore.collection("Task").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("list").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    //notify the user if task is successfully added
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    //give the user an error if task is not saved
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //give error message if failure to add the task happened
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                    dismiss();
                    progressBar.setVisibility(View.GONE);
                }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof  OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }

    public interface OnDialogCloseListener{
        void onDialogClose(DialogInterface dialogInterface);
    }

}