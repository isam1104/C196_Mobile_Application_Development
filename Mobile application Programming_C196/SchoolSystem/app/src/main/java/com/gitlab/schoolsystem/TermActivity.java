package com.gitlab.schoolsystem;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TermActivity extends AppCompatActivity{
    public static final String TITLE =
            "com.gitlab.schoolsystem.TermModel";
    private TermAdapter termAdapter;
    private TermModelViewModel termModelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        RecyclerView recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        termAdapter = new TermAdapter();
        recyclerView.setAdapter(termAdapter);

        termModelViewModel = ViewModelProviders.of(this).get(TermModelViewModel.class);
        termModelViewModel.getAllTerms().observe(this, termModels -> termAdapter.setTermList(termModels));

        // set termViewModel
        termAdapter.setCourseGridRequirements(termModelViewModel, this);
        // toast information
        Toast.makeText(this, "Swipe left/right to delete cards", Toast.LENGTH_LONG).show();

        // request permission to use the  ALARM
        if(checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, 0);
        }
        FloatingActionButton add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TermDialog(TermActivity.this, termModelViewModel, null, true).show();
            }
        });
        // add swipe right or left to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TermModel termModelToDelete = termAdapter.getTermAt(viewHolder.getAdapterPosition());
                LiveData<Integer> coursesLiveData = termModelViewModel.getCoursesByTermID(termModelToDelete.getId());
                coursesLiveData.observe(TermActivity.this, count -> {
                    if(count > 0)
                    {
                        Toast.makeText(TermActivity.this, "Term has courses, cannot be deleted", Toast.LENGTH_SHORT).show();
                        termAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }else
                    {
                        termModelViewModel.delete(termModelToDelete);
                        Toast.makeText(TermActivity.this, "Term  deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);
        // set listeners
        termAdapter.setListenerOnClick(new TermAdapter.OnTermListener() {
            @Override
            public void onTermClicked(TermModel term) {
                Intent  intent  = new Intent(TermActivity.this, CourseActivity.class);
                intent.putExtra(TermActivity.TITLE, term);
                startActivity(intent);
            }

            @Override
            public void onUpdateButtonClicked(TermModel term) {
                // create a dialog that reports back here
                new TermDialog(TermActivity.this,  termModelViewModel, term, false).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Swipe left/right to delete cards", Toast.LENGTH_LONG).show();
        termAdapter.notifyDataSetChanged();
    }
}