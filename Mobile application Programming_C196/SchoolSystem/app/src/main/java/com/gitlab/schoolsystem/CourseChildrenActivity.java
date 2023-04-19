package com.gitlab.schoolsystem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.gitlab.schoolsystem.ui.main.SectionsPagerAdapter;
import com.gitlab.schoolsystem.databinding.ActivityCourseChildrenBinding;

public class CourseChildrenActivity extends AppCompatActivity {

    public static final String TERM_MODEL =
            "com.gitlab.schoolsystem.TermModel";
    public static final String COURSE_MODEL =
            "com.gitlab.schoolsystem.CourseModel";

    private ActivityCourseChildrenBinding binding;
    private CourseModel courseModel;
    private TermModel termModel;

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        if(intent != null){
            intent.putExtra(TERM_MODEL, termModel);
            intent.putExtra(COURSE_MODEL, courseModel);
            setResult(RESULT_OK);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCourseChildrenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        // read intent
        if(getIntent().hasExtra(COURSE_MODEL)){
            courseModel = getIntent().getParcelableExtra(COURSE_MODEL);
//            Log.d(TAG, "onCreate: " + courseModel.getId());
        }
        if(getIntent().hasExtra(TERM_MODEL))
        {
            termModel = getIntent().getParcelableExtra(TERM_MODEL);
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(courseModel.getCourse_title());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public CourseModel getCourseModel(){ return this.courseModel; }
    public TermModel getTermModel(){ return termModel; }

}