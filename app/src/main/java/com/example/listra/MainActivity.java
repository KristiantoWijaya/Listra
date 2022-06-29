package com.example.listra;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listra.adapter.TaskAdapter;
import com.example.listra.model.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTask.OnDialogCloseListener{
    private ImageView logout;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db ;
    private TaskAdapter adapter;
    private List<TaskModel> mList;
    private String userID;
    private ImageView emptyIllustration;
    private TextView emptyText;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = (ImageView) findViewById(R.id.back_main);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyIllustration = findViewById(R.id.NoTaskIllust);
        emptyText = findViewById(R.id.Dontworry);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);

        //check if user is signed in or not
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //get the user's UID if the user is logged in, else redirect the user to the login page
            userID = currentUser.getUid();

        }else if (currentUser == null) {
            startActivity(new Intent(this, LoginPage.class));
        }

        //give blank page when user first open the app
        emptyIllustration.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);

        mList = new ArrayList<>();
        adapter = new TaskAdapter(MainActivity.this, mList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
        showData(userID);

        //onSwipe delete and edit function call
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        //onclick listener for add new task button
        mFab = findViewById(R.id.AddButton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTask.newInstance().show(getSupportFragmentManager(), AddTask.TAG);
            }
        });

        //update the data upon refresh swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                showData(userID);
                //listenerRegistration.remove();
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "View Refreshed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //onclick listener for logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user clicked the log out button, user will be redirected the the login page
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginPage.class));
                Toast.makeText(MainActivity.this, "Sign out successful", Toast.LENGTH_LONG).show();
            }
        });
    }


//function to show data on any changes made
    private void showData(String userID){
        progressBar.setVisibility(View.VISIBLE);
        query = db.collection("Task").document(userID)
                .collection("list").orderBy("time");
        // function is making queries to firestore database
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        TaskModel taskModel = documentChange.getDocument().toObject(TaskModel.class).withId(id);
                        mList.add(taskModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                    //check if tasklist is empty
                checkView();
                    //removing listener from firestore so task wont show twice
                listenerRegistration.remove();
                progressBar.setVisibility(View.GONE);
                }

        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData(userID);
        adapter.notifyDataSetChanged();
    }
    public  void checkView (){
        if (adapter.getItemCount() == 0){
            emptyIllustration.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            emptyIllustration.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
    }

}