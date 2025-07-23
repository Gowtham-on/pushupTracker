package com.cmp.pushuptracker.database.repository


import com.cmp.pushuptracker.database.dao.UserDao
import com.cmp.pushuptracker.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val dao: UserDao
) {
    val userData: Flow<List<UserEntity>> = dao.getUserData()

    fun getUserDataFromDb(): Flow<List<UserEntity>> {
        return dao.getUserData()
    }

    suspend fun addUser(reps: Int, name: String, best: Int) {
        dao.insertUser(UserEntity(totalReps = reps, best = best, name = name))
    }

    suspend fun delete(user: UserEntity) {
        dao.deleteUser(user)
    }

    suspend fun updateSession(user: UserEntity) {
        dao.updateUser(user)
    }
}
