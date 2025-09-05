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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class PushupViewModel @Inject constructor(
    private val repository: PushupRepository
) : ViewModel() {
    // All sessions
    val pushupData: StateFlow<List<PushUpEntity>> = repository
        .pushupDataFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(PushUpEntity("", 0, 0, 0))
        )

    // Today’s session as a StateFlow
    private val today: String = TimeUtils.getTodayDate("dd/MM/yyyy")
    val todayData: StateFlow<PushUpEntity?> = (repository.getSessionByDate(today) ?: flowOf(null))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // Selected date handling via a StateFlow
    private val selectedDate = MutableStateFlow<String?>(null)
    val selectedDayData: StateFlow<PushUpEntity?> = selectedDate
        .flatMapLatest { date ->
            if (date.isNullOrBlank()) flowOf(null) else (repository.getSessionByDate(date) ?: flowOf(null))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }

    // Backward-compatible alias
    fun getRecordByDate(date: String) = setSelectedDate(date)

    fun addPushupRecord(reps: Int, duration: Int, sets: Int, date: String) {
        viewModelScope.launch {
            repository.addSession(date, reps, duration, sets)
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
}
