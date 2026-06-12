package com.cmp.pushuptracker.database.repository

import com.cmp.pushuptracker.database.dao.PushUpDao
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.utils.TimeUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushupRepository @Inject constructor(
  private val dao: PushUpDao
) {
  val pushupDataFlow: Flow<List<PushUpEntity>> = dao.getAllSessionsDesc()

  suspend fun addSession(date: String, reps: Int, duration: Int, sets: Int) {
    dao.insertSession(PushUpEntity(date = TimeUtils.toStorageDate(date), reps = reps, duration = duration, sets = sets))
  }

  suspend fun delete(session: PushUpEntity) {
    dao.deleteSession(session)
  }

  suspend fun updateSession(session: PushUpEntity) {
    dao.updateSession(session)
  }

  fun getSessionByDate(date: String): Flow<PushUpEntity?>? {
    return dao.getSessionByDateFlow(TimeUtils.toStorageDate(date))
  }
}
