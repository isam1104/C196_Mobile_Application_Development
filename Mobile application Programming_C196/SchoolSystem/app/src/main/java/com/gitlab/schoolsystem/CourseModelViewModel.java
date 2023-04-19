package com.gitlab.schoolsystem;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

public class CourseModelViewModel extends AndroidViewModel {
    private CourseModelRepository courseModelRepository;
    private LiveData<List<CourseModel>> allCourses;
    public CourseModelViewModel(@NonNull Application application) {
        super(application);
        courseModelRepository = new CourseModelRepository(application);
        allCourses = courseModelRepository.getAllCourses();
    }
    public void insert(CourseModel course){
        courseModelRepository.insert(course);
    }
    public void update(CourseModel course){
        courseModelRepository.update(course);
    }
    public void delete(CourseModel course){
        courseModelRepository.delete(course);
    }
    public void deleteAllNotes(){
        courseModelRepository.deleteAll();
    }
    public LiveData<List<CourseModel>> getAllCourses(){
        return allCourses;
    }
    public LiveData<List<CourseModel>> getCoursesByTermID(UUID term_id){
        return courseModelRepository.getCoursesByTermID(term_id);
    }
    public LiveData<CourseModel> getCourseId(int courseId)
    {
        return courseModelRepository.getCourseById(courseId);
    }
}
