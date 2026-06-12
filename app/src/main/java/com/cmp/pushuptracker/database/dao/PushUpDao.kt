package com.cmp.pushuptracker.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cmp.pushuptracker.database.entity.PushUpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PushUpDao {
    @Query("SELECT * FROM pushup_table ORDER BY date DESC")
    fun getAllSessionsDesc(): Flow<List<PushUpEntity>>

    /** Insert or replace if the same date already exists. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PushUpEntity)

    /** Update an existing session (matched by date primary key). */
    @Update
    suspend fun updateSession(session: PushUpEntity)

    /** Delete a session. */
    @Delete
    suspend fun deleteSession(session: PushUpEntity)

    /** Lookup a session by its date string. */
    @Query(
        """
    SELECT * 
      FROM pushup_table 
     WHERE date = :date 
     LIMIT 1
  """
    )
    fun getSessionByDateFlow(date: String): Flow<PushUpEntity?>
}
