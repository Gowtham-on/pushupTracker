package com.cmp.pushuptracker.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.database.repository.PushupRepository
import com.cmp.pushuptracker.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushupViewModel @Inject constructor(
    private val repository: PushupRepository
) : ViewModel() {
    val pushupData: StateFlow<List<PushUpEntity>> = repository
        .pushupDataFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    var todayData by mutableStateOf<PushUpEntity?>(null)

    init {
        val today = TimeUtils.getTodayDate("dd/MM/yyyy")
        getRecordByDate(today)
    }

    fun addPushupRecord(reps: Int, duration: Int, sets: Int, date: String) {
        viewModelScope.launch {
            repository.addSession(date, reps, duration, sets)
            getRecordByDate(date)
        }
    }

    fun updateRecord(session: PushUpEntity) {
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }

    fun deleteSession(session: PushUpEntity) {
        viewModelScope.launch {
            repository.delete(session)
        }
    }

    fun getRecordByDate(date: String) {
        viewModelScope.launch {
            repository.getSessionByDate(date)?.collect {
                todayData = it
            }
        }
    }
}