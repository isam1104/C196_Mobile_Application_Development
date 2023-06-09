package com.gitlab.schoolsystem;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.UUID;

public class CourseModelRepository {
    private static final String DATABASE = "ss_database";
    private CourseModelDao courseModelDao;
    private LiveData<List<CourseModel>> courseModelList;
    public CourseModelRepository(Application application){
        AppDatabase database = Room.databaseBuilder(application, AppDatabase.class, DATABASE).build();
        courseModelDao = database.courseModelDao();
        courseModelList = courseModelDao.getAllCourses();
    }
    public void insert(CourseModel course){
        new InsertCourseModelAsyncTask(courseModelDao).execute(course);
    }
    public void update(CourseModel course){
        new UpdateCourseModelAsyncTask(courseModelDao).execute(course);
    }
    public void delete(CourseModel course){
        new DeleteCourseModelAsyncTask(courseModelDao).execute(course);
    }
    public void deleteAll(){
        new DeleteCourseModelAsyncTask(courseModelDao).execute();
    }
    public LiveData<List<CourseModel>> getAllCourses(){
        return courseModelList;
    }
    public LiveData<List<CourseModel>>  getCoursesByTermID(UUID term_id){
        return courseModelDao.getCoursesByTermID(term_id);
    }
    public LiveData<CourseModel> getCourseById(int courseId){
        return courseModelDao.getCourseById(courseId);
    }
    private static class InsertCourseModelAsyncTask extends AsyncTask<CourseModel, Void, Void>{
        private CourseModelDao courseModelDao;
        private InsertCourseModelAsyncTask(CourseModelDao courseModelDao1){
            this.courseModelDao = courseModelDao1;
        }
        @Override
        protected Void doInBackground(CourseModel... courseModels) {
            courseModelDao.insert(courseModels[0]);
            return null;
        }
    }
    private static class UpdateCourseModelAsyncTask extends AsyncTask<CourseModel, Void, Void>{
        private CourseModelDao courseModelDao;
        private UpdateCourseModelAsyncTask(CourseModelDao courseModelDao){
            this.courseModelDao = courseModelDao;
        }
        @Override
        protected Void doInBackground(CourseModel... courseModels) {
            courseModelDao.update(courseModels[0]);
            return null;
        }
    }
    private static class DeleteCourseModelAsyncTask extends AsyncTask<CourseModel, Void, Void>{
        private CourseModelDao courseModelDao;
        private DeleteCourseModelAsyncTask(CourseModelDao courseModelDao){
            this.courseModelDao = courseModelDao;
        }
        @Override
        protected Void doInBackground(CourseModel... courseModels) {
            courseModelDao.delete(courseModels[0]);
            return null;
        }
    }
    private static class DeleteAllCourseModelAsyncTask extends AsyncTask<Void, Void, Void>{
        private CourseModelDao courseModelDao;
        private DeleteAllCourseModelAsyncTask(CourseModelDao courseModelDao){
            this.courseModelDao = courseModelDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            courseModelDao.deleteAllCourses();
            return null;
        }
    }
}
