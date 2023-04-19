package com.gitlab.schoolsystem;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "note_table")
public class NoteModel {

    @PrimaryKey
            @NonNull
            @ColumnInfo(name = "id")
    private UUID id;

    @ForeignKey(entity = CourseModel.class, parentColumns = "id", childColumns = "course_id", onDelete = CASCADE)
    private UUID course_id;

    public String note_title,
    node_body;

    public NoteModel(String note_title, String node_body) {
        this.id = UUID.randomUUID();
        this.note_title = note_title;
        this.node_body = node_body;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNode_body() {
        return node_body;
    }

    public void setNode_body(String node_body) {
        this.node_body = node_body;
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

    @Override
    public String toString() {
        return "NoteModel{" +
                "course_id='" + course_id.toString() + '\'' +
                ", note_title='" + note_title + '\'' +
                ", node_body='" + node_body + '\'' +
                '}';
    }
}
