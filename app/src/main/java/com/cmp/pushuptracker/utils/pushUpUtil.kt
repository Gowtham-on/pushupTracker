package com.cmp.pushuptracker.utils

import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.database.entity.UserEntity
import com.cmp.pushuptracker.ui.screen.home.model.PushupQuickAdd
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel

object PushupUtils {
    fun addPushupInDb(
        selectedDayData: PushUpEntity? = null,
        selectedDate: String,
        userData: UserEntity,
        addPushupData: PushupQuickAdd,
        userViewmodel: UserViewmodel,
        pushupViewModel: PushupViewModel
    ) {
        var tempData = selectedDayData ?: PushUpEntity(
            selectedDate,
            0,
            0,
            0
        )

        val user = userData
        user.totalReps += addPushupData.reps.toInt()
        if (user.best < addPushupData.reps.toInt() == true) {
            user.best = addPushupData.reps.toInt()
        }
        user.totalWorkoutDuration += tempData.duration + (addPushupData.min.toInt() * 60) + addPushupData.secs.toInt()
        userViewmodel.updateUserData(userData)
        pushupViewModel.addPushupRecord(
            reps = addPushupData.reps.toInt() + tempData.reps,
            sets = addPushupData.sets.toInt() + tempData.sets,
            duration = ((addPushupData.min.toInt() * 60) + addPushupData.secs.toInt()) + tempData.duration,
            date = selectedDate
        )
    }
}