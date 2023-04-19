package com.gitlab.schoolsystem;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TermModel.class, CourseModel.class, AssessmentModel.class, NoteModel.class}, version = 1)
@TypeConverters({UUIDConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TermModelDao termModelDao();
    public abstract CourseModelDao courseModelDao();
    public abstract AssessmentModelDao assessmentModelDao();
    public abstract  NoteModelDao  noteModelDao();
}
