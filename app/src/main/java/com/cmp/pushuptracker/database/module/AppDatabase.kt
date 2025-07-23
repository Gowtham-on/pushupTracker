package com.cmp.pushuptracker.database.module

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cmp.pushuptracker.database.dao.PushUpDao
import com.cmp.pushuptracker.database.dao.UserDao
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.database.entity.UserEntity


@Database(entities = [PushUpEntity::class, UserEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pushupDao(): PushUpDao
    abstract fun userDao(): UserDao
}
