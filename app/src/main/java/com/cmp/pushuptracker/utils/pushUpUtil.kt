package com.cmp.pushuptracker.utils

import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.database.entity.UserEntity
import com.cmp.pushuptracker.ui.screen.home.model.PushupQuickAdd
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel

object PushupUtils {
    fun buildPushupMerge(
        selectedDayData: PushUpEntity?,
        selectedDate: String,
        addPushupData: PushupQuickAdd
    ): PushupMergeResult {
        val storageDate = TimeUtils.toStorageDate(selectedDate)
        val tempData = selectedDayData ?: PushUpEntity(
            storageDate,
            0,
            0,
            0
        )
        val newReps = addPushupData.reps.toIntOrNull() ?: 0
        val newSets = addPushupData.sets.toIntOrNull() ?: 0
        val newDuration = ((addPushupData.min.toIntOrNull() ?: 0) * 60) +
                (addPushupData.secs.toIntOrNull() ?: 0)

        return PushupMergeResult(
            session = PushUpEntity(
                date = storageDate,
                reps = newReps + tempData.reps,
                sets = newSets + tempData.sets,
                duration = newDuration + tempData.duration
            ),
            addedReps = newReps,
            addedDuration = newDuration
        )
    }

    fun addPushupInDb(
        selectedDayData: PushUpEntity? = null,
        selectedDate: String,
        userData: UserEntity,
        addPushupData: PushupQuickAdd,
        userViewmodel: UserViewmodel,
        pushupViewModel: PushupViewModel
    ) {
        val merge = buildPushupMerge(selectedDayData, selectedDate, addPushupData)

        val user = userData
        user.totalReps += merge.addedReps
        if (user.best < merge.addedReps) {
            user.best = merge.addedReps
        }
        user.totalWorkoutDuration += merge.addedDuration
        userViewmodel.updateUserData(userData)
        pushupViewModel.addPushupRecord(
            reps = merge.session.reps,
            sets = merge.session.sets,
            duration = merge.session.duration,
            date = merge.session.date
        )
    }
}

data class PushupMergeResult(
    val session: PushUpEntity,
    val addedReps: Int,
    val addedDuration: Int
)
