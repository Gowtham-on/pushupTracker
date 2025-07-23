package com.cmp.pushuptracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmp.pushuptracker.database.entity.UserEntity
import com.cmp.pushuptracker.database.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewmodel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    var userData by mutableStateOf(UserEntity())

    init {
        viewModelScope.launch {
            repository.getUserDataFromDb().collect {
                userData = it.firstOrNull() ?: UserEntity()
            }
        }
    }

    fun updateUserData(user: UserEntity) {
        viewModelScope.launch {
            repository.updateSession(user)

        }
    }

    fun addUser(name: String, best: Int, reps: Int) {
        viewModelScope.launch {
            repository.addUser(reps, name, best)
        }
    }
}