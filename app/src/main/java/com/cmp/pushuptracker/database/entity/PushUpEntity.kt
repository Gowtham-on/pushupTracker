package com.cmp.pushuptracker.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pushup_table")
data class PushUpEntity(
    @PrimaryKey(autoGenerate = false) val date: String = "",
    var reps: Int,
    var sets: Int,
    var duration: Int,
)