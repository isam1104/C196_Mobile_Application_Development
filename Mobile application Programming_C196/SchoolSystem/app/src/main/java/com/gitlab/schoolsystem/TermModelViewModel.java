package com.gitlab.schoolsystem;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

public class TermModelViewModel extends AndroidViewModel {
    private TermModelRepository termModelRepository;
    private LiveData<List<TermModel>> allTerms;
    public TermModelViewModel(@NonNull Application application) {
        super(application);
        termModelRepository = new TermModelRepository(application);
        allTerms = termModelRepository.getAllTermModels();
    }
    public void insert(TermModel term){
        termModelRepository.insert(term);
    }
    public void update(TermModel term){
        termModelRepository.update(term);
    }
    public void delete(TermModel term){
        termModelRepository.delete(term);
    }
    public void deleteAllNotes(){
        termModelRepository.deleteAll();
    }
    public LiveData<List<TermModel>> getAllTerms(){
        return allTerms;
    }
    public LiveData<Integer> getCoursesByTermID(UUID term_id){
        return termModelRepository.getCoursesByTermID(term_id);
    }

    public LiveData<List<CourseModel>> getAllCoursesBelongingToTerm(UUID term_id)
    {
        return termModelRepository.getAllCoursesBelongingToTerm(term_id);
    }
}
