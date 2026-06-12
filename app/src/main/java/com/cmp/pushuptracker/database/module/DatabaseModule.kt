package com.cmp.pushuptracker.database.module

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cmp.pushuptracker.database.dao.PushUpDao
import com.cmp.pushuptracker.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                UPDATE pushup_table
                   SET date = substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)
                 WHERE date LIKE '__/__/____'
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "pushup_db")
            .addMigrations(MIGRATION_3_4)
            .build()

    @Provides
    @Singleton
    fun provideSessionDao(db: AppDatabase): PushUpDao =
        db.pushupDao()


    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao =
        db.userDao()

}
