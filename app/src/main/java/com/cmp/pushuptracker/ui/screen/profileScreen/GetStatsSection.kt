package com.cmp.pushuptracker.ui.screen.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.database.entity.UserEntity
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.TimeUtils.getMinsSecFromSeconds
import com.cmp.pushuptracker.utils.estimatePushupCalories

@Composable
fun GetStatsSection(userData: UserEntity) {
    val reps = remember { userData.totalReps }
    val best = remember { userData.best }
    val totalDuration = remember { getMinsSecFromSeconds(userData.totalWorkoutDuration) }
    var totalCaloriesBurnt =
        remember { estimatePushupCalories(reps, userData.totalWorkoutDuration, userData.weight) }

    LaunchedEffect(reps,) {
        totalCaloriesBurnt = estimatePushupCalories(reps, userData.totalWorkoutDuration, userData.weight)

    }
    Column {
        Text(
            "Achievements",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(state = rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            GetIllustrationStatsCard(
                reps.toString(),
                "Total Reps",
                Modifier.weight(1f),
                R.drawable.pushup_done_illustration
            )
            GetIllustrationStatsCard(
                totalDuration,
                "Total Duration",
                Modifier.weight(1f),
                R.drawable.duration_ill
            )
            GetIllustrationStatsCard(
                totalCaloriesBurnt,
                "Total Calories Burnt",
                Modifier.weight(1f),
                R.drawable.calories_ill
            )
            GetIllustrationStatsCard(
                best.toString(),
                "Personal Best",
                Modifier.weight(1f),
                R.drawable.personal_best_ill
            )
        }
    }
}

@Composable
fun GetIllustrationStatsCard(count: String, title: String, modifier: Modifier, illustration: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            modifier = Modifier.size(150.dp)
        ) {
            Image(
                painter = painterResource(illustration),
                contentDescription = "Title",
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            title,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            count,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
