package com.gitlab.schoolsystem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface AssessmentModelDao {
    @Insert
    void insert(AssessmentModel assessment);

    @Update
    void update(AssessmentModel assessment);

    @Delete
    void delete(AssessmentModel assessment);

    @Query("DELETE FROM assessment_table")
    void deleteAllAssessments();

    @Query("SELECT * FROM assessment_table ORDER BY id DESC")
    LiveData<List<AssessmentModel>> getAllAssessments();

    @Query("SELECT * FROM assessment_table WHERE course_id = :course_id")
    LiveData<List<AssessmentModel>> getAssessmentsByCourseID(UUID course_id);
}
