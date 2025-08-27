package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

    var currentMode by mutableIntStateOf(CurrentMode.INIT.ordinal)
        private set

    var reps by mutableIntStateOf(0)
        private set

    var sets by mutableIntStateOf(0)
        private set

    var interval by mutableIntStateOf(0)
        private set

    var totalReps by mutableIntStateOf(0)
        private set

    var totalSets by mutableIntStateOf(0)
        private set

    fun incrementTotalReps() {
        totalReps += 1
    }

    fun incrementSets() {
        totalSets += 1
    }

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

    fun clearData() {
        setCurrentMode(CurrentMode.INIT)
        setupPushupDataValues(0, 0, 0)
        totalReps = 0
        totalSets = 0
    }

    //////////////////////////////////////////////////////////////////////////////
    var currentRep by mutableIntStateOf(0)
        private set

    var currentPhase = CurrentPhase.UP
        private set

    var minShoulderY by mutableFloatStateOf(Float.MAX_VALUE)

    var maxShoulderY by mutableFloatStateOf(Float.MIN_VALUE)

    fun setLivePreviewPhase(phase: CurrentPhase) {
        if (currentPhase == CurrentPhase.DOWN && phase == CurrentPhase.UP) {
            currentRep += 1
        }
        currentPhase = phase
    }
    //////////////////////////////////////////////////////////////////////////////
}

enum class CurrentMode {
    INIT,
    INTERVAL,
    PUSHUP,
    COMPLETED
}

enum class CurrentPhase {
    DOWN,
    UP
}