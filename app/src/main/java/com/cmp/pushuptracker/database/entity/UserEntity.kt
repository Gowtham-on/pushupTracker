package com.cmp.pushuptracker.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var totalReps: Int = 0,
    var best: Int = 0,
    var name: String = "",
)