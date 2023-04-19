package com.gitlab.schoolsystem;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CourseDetailsFragment extends Fragment {
    private static final String TAG = "CourseDetailsFragment";

    private View view;
    private FloatingActionButton save_button;
    private EditText course_title, start_date, end_date;
    private Spinner status_spinner, instructor_spinner;

    private CourseModelViewModel courseModelViewModel;


    private AtomicReference<String> selected_status;
    private AtomicReference<String> selected_instructor;
    private CourseModel courseModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_details, container, false);
        save_button = view.findViewById(R.id.save_button);

        courseModel = ((CourseChildrenActivity) requireActivity()).getCourseModel();

        courseModelViewModel = ViewModelProviders.of(requireActivity()).get(CourseModelViewModel.class);

        setElements();
        save_button.setOnClickListener(view -> saveElements());
        return view;
    }
    private void setElements(){
        course_title = view.findViewById(R.id.coursetitle_edit);
        course_title.setText(courseModel.getCourse_title());

        start_date = view.findViewById(R.id.startdate_edit);
        start_date.setText(courseModel.getCourse_start_date());
        new DatePicker(requireContext(), start_date).enable();

        end_date = view.findViewById(R.id.enddate_edit);
        end_date.setText(courseModel.getCourse_end_date());
        new DatePicker(requireContext(), end_date).enable();

        status_spinner  = view.findViewById(R.id.status_spinner);
        status_spinner.setSelection(courseModel.getCourse_status().ordinal());

        //  course status spinner
        String[] status_resource_array = requireContext().getResources().getStringArray(R.array.course_status_list);
        selected_status  = new AtomicReference<>();
        setupSpinner(status_spinner, status_resource_array, (pair)->{
            Spinner spinner = pair.first;
            selected_status.set(pair.second);
        } );
        // instructor spinner
        instructor_spinner = view.findViewById(R.id.instructor_spinner);
        LiveData<List<CourseModel>> liveCourseModels  = courseModelViewModel.getAllCourses();
        selected_instructor = new AtomicReference<>();
        setupSpinner(instructor_spinner, new String[]{}, (pair)->{
            Spinner spinner = pair.first;
            selected_instructor.set(pair.second);
        });
        liveCourseModels.observe(getViewLifecycleOwner(), new Observer<List<CourseModel>>() {
            @Override
            public void onChanged(List<CourseModel> courseModels) {
                // Create a HashSet to store the unique instructor names from the courses
                Set<String> instructorSet = new HashSet<>();
                for (CourseModel course : courseModels) {
                    String instructor = course.getCourse_instructor().toString();
                    if (instructor != null && !instructor.isEmpty()) {
                        instructorSet.add(instructor);
                    }
                }
                String[] instructorArray = instructorSet.toArray(new String[0]);
                int spinnerResource  = R.layout.custom_spinner;
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), spinnerResource, instructorArray);
                instructor_spinner.setAdapter(spinnerAdapter);
            }
        });
    }

    private void saveElements(){
        String course_title_ = course_title.getText().toString();
        String start_date_ =  start_date.getText().toString();
        String end_date_ = end_date.getText().toString();

        if(Utils.compareDates(start_date_, end_date_) < 0){
            courseModel.setCourse_title(course_title_);
            courseModel.setCourse_start_date(start_date_);
            courseModel.setCourse_end_date(end_date_);
            courseModel.setCourse_status(CourseStatus.valueOf(selected_status.toString()));
            courseModel.setCourse_instructor(new InstructorModel(String.valueOf(selected_instructor)));
        }else {
            Toast.makeText(requireContext(), "Wrong dates order", Toast.LENGTH_LONG).show();
        }
//        Log.d(TAG, "saveElements: "+  course_title_ + " | "+  start_date_ + " | "+  end_date_ + " | "+  selected_status + " | " + selected_instructor);
    }
    private void setupSpinner(final Spinner spinner , String[] resource_array, Consumer<Pair<Spinner, String>> callback){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.custom_spinner,
                resource_array
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spinner.getItemAtPosition(i).toString();
                callback.accept(new Pair<>(spinner, selectedItem));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
