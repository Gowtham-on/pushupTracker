package com.cmp.pushuptracker.camera

import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentPhase

data class PushUpCounterUpdate(
    val count: Int,
    val phase: CurrentPhase,
    val counted: Boolean
)

class PushUpCounterEngine(
    private val downAngleMax: Float = 90f,
    private val upAngleMin: Float = 155f,
    private val minRepIntervalMs: Long = 500L,
) {
    var count: Int = 0
        private set

    var phase: CurrentPhase = CurrentPhase.UP
        private set

    private var lastCountAt = 0L

    fun update(armAngle: Float, nowMs: Long = System.currentTimeMillis()): PushUpCounterUpdate {
        if (phase == CurrentPhase.UP && armAngle <= downAngleMax) {
            phase = CurrentPhase.DOWN
        }

        if (phase == CurrentPhase.DOWN && armAngle >= upAngleMin) {
            phase = CurrentPhase.UP
            if (nowMs - lastCountAt >= minRepIntervalMs) {
                count++
                lastCountAt = nowMs
                return PushUpCounterUpdate(count, phase, counted = true)
            }
        }

        return PushUpCounterUpdate(count, phase, counted = false)
    }

    fun reset() {
        count = 0
        phase = CurrentPhase.UP
        lastCountAt = 0L
    }
}
