package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cmp.pushuptracker.database.repository.PushupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale

@HiltViewModel
class LivePreviewViewmodel @Inject constructor(
    private val repository: PushupRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), TextToSpeech.OnInitListener {

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
        currentRep = 0
        updateCounterDebug(CurrentPhase.UP, 0f, null, true)
    }

    //////////////////////////////////////////////////////////////////////////////
    var currentRep by mutableIntStateOf(0)
        private set

    var currentPhase = CurrentPhase.UP
        private set

    var counterPhase by mutableStateOf(CurrentPhase.UP.name)
        private set

    var lastArmAngle by mutableFloatStateOf(0f)
        private set

    var lastBodyLineAngle by mutableFloatStateOf(0f)
        private set

    var isBodyLineOk by mutableStateOf(true)
        private set

    var minShoulderY by mutableFloatStateOf(Float.MAX_VALUE)

    var maxShoulderY by mutableFloatStateOf(Float.MIN_VALUE)

    fun setLivePreviewPhase(phase: CurrentPhase) {
        if (currentPhase == CurrentPhase.DOWN && phase == CurrentPhase.UP) {
            currentRep += 1
        }
        currentPhase = phase
        counterPhase = phase.name
    }

    fun updateCounterDebug(
        phase: CurrentPhase,
        armAngle: Float,
        bodyLineAngle: Float?,
        bodyLineOk: Boolean
    ) {
        currentPhase = phase
        counterPhase = phase.name
        lastArmAngle = armAngle
        lastBodyLineAngle = bodyLineAngle ?: 0f
        isBodyLineOk = bodyLineOk
    }
    //////////////////////////////////////////////////////////////////////////////

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    var isNoseVisibleInCamera by mutableStateOf(false)

    fun setNoseVisible(visible: Boolean) {
        isNoseVisibleInCamera = visible
    }

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)
            ttsReady = true
        }
    }

    fun speak(message: String, flush: Boolean = true) {
        if (!ttsReady) return
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(message, mode, null, "live-preview-tts")
    }

    override fun onCleared() {
        tts?.shutdown()
        tts = null
        super.onCleared()
    }
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
