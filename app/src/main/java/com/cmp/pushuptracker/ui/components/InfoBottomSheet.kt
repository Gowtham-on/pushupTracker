package com.cmp.pushuptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.screen.home.GetHomePushupCard
import com.cmp.pushuptracker.utils.PushupIllustrations
import com.cmp.pushuptracker.utils.TimeUtils
import com.cmp.pushuptracker.utils.estimatePushupCalories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(pushupData: PushUpEntity?, closeSheet: () -> Unit) {

    ModalBottomSheet(
        onDismissRequest = closeSheet,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            GetHomePushupCard(
                "Reps",
                (pushupData?.reps ?: 0).toString(),
                "Push-Ups",
                illustrationType = PushupIllustrations.ONE,
                canShowIllustration = false
            )
            GetHomePushupCard(
                "Time",
                (TimeUtils.getMinsSecFromSeconds(
                    pushupData?.duration?.toLong() ?: 0L
                )).toString(),
                "Workout duration",
                illustrationType = PushupIllustrations.TWO,
                canShowIllustration = false
            )
            GetHomePushupCard(
                "Calories",
                estimatePushupCalories(
                    reps = pushupData?.reps ?: 0,
                    durationSec = pushupData?.duration ?: 0,
                    weightKg = 70.0,
                ),
                "Estimated Calories burnt",
                illustrationType = PushupIllustrations.THREE,
                canShowIllustration = false
            )
            Spacer(Modifier.height(10.dp))
        }

    }

}