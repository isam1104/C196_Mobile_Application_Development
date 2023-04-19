package com.gitlab.schoolsystem;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.UUID;

@Entity(tableName = "assessment_table")
public class AssessmentModel {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name  = "id")
    private UUID id;

    @ForeignKey(entity = CourseModel.class, parentColumns = "id", childColumns = "course_id", onDelete = CASCADE)
    private UUID course_id;

    private String name, start_date,due_date;

    @TypeConverters(AssessmentTypeConverter.class)
    private AssessmentType assessmentType;

    public AssessmentModel(String name, String start_date, String due_date, AssessmentType assessmentType) {
        this.id = UUID.randomUUID();

        this.name = name;
        this.start_date = start_date;
        this.due_date = due_date;
        this.assessmentType = assessmentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourse_id() {
        return course_id;
    }

    public void setCourse_id(UUID course_id) {
        this.course_id = course_id;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    @Override
    public String toString() {
        return "AssessmentModel{" +
                "id=" + id +
                ", course_title='" + course_id.toString() + '\'' +
                ", name='" + name + '\'' +
                ", start_date='" + start_date + '\'' +
                ", due_date='" + due_date + '\'' +
                '}';
    }
}
