package com.example.listra;


import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listra.adapter.TaskAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter adapter;

    public RecyclerItemTouchHelper(TaskAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
//delete or edit the task depending on the swipe direction
    //task will get deleted on swipe left and edited on swipe right
    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            //if task is swiped to the left
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            //give user an alert message to confirm task deletion
            builder.setTitle("Delete Task");
            builder.setMessage("Confirm Deletion");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //calling the deleteTask method to delete the task
                            adapter.deleteTask(position);
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            //stop user from clicking outside of the alert message
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            adapter.editTask(position);
        }
    }

    //change icon and background of behind the task on swipe
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightActionIcon(R.drawable.ic_baseline_delete)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_edit)
                .addSwipeLeftBackgroundColor(Color.BLUE)
                .create()
                .decorate();
    }
}
