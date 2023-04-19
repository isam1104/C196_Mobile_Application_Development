package com.gitlab.schoolsystem;

import androidx.room.TypeConverter;

public class AssessmentTypeConverter {
    @TypeConverter
    public static AssessmentType fromString(String value){
        return AssessmentType.valueOf(value);
    }
    @TypeConverter
    public static String fromEnum(AssessmentType assessmentType){
        return assessmentType.name();
    }
}
