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
public interface NoteModelDao {
    @Insert
    void insert(NoteModel note);

    @Update
    void update(NoteModel note);

    @Delete
    void delete(NoteModel note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    LiveData<List<NoteModel>> getAllNotes();

    @Query("SELECT * FROM note_table WHERE course_id = :course_id")
    LiveData<List<NoteModel>> getNotesByCourseID(UUID course_id);
}
