package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cmp.pushuptracker.database.repository.PushupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LivePreviewViewmodel @Inject constructor(
    private val repository: PushupRepository  // optional dependency
) : ViewModel() {

    var currentMode by mutableIntStateOf(CurrentMode.PUSHUP.ordinal)
        private set

    var reps by mutableIntStateOf(0)
        private set
    var sets by mutableIntStateOf(0)
        private set
    var interval by mutableIntStateOf(0)
        private set

    fun setupPushupDataValues(
        sets: Int,
        reps: Int,
        interval: Int
    ) {
        this.reps = reps
        this.sets = sets
        this.interval = interval
    }

    fun setCurrentMode(mode: CurrentMode) {
        currentMode = mode.ordinal
    }

    //////////////////////////////////////////////////////////////////////////////
    var currentRep by mutableIntStateOf(0)
        private set

    var currentPhase = CurrentPhase.UP
        private set

    fun setLivePreviewPhase(phase:CurrentPhase) {
        if (currentPhase == CurrentPhase.DOWN && phase == CurrentPhase.UP) {
            currentRep += 1
        }
        currentPhase = phase
    }
    //////////////////////////////////////////////////////////////////////////////
}

enum class CurrentMode {
    INTERVAL,
    PUSHUP
}

enum class CurrentPhase {
    DOWN,
    UP
}