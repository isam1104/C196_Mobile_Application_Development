package com.gitlab.schoolsystem;

import androidx.room.TypeConverter;

import java.util.UUID;

public class UUIDConverter {
    @TypeConverter
    public static UUID fromString(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}
