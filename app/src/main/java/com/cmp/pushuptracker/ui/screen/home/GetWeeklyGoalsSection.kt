package com.cmp.pushuptracker.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.BarGraph
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.calculateWeeklyCount
import com.cmp.pushuptracker.utils.getWeeklyReps

@Composable
fun GetWeeklyGoalsSection(pushups: List<PushUpEntity>) {
    val weeklyCount = remember(pushups) { calculateWeeklyCount(pushups) }
    Text(
        "Weekly Goals",
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(Modifier.height(12.dp))
    Column {
        Text(
            "Push-Ups",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            weeklyCount.toString(),
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))

    }
    if (weeklyCount > 0) {
        BarGraph(
            counts = getWeeklyReps(pushups),
            barColor = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))
    }
}