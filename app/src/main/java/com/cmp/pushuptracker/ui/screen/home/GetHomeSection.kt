package com.cmp.pushuptracker.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.GetHabitCalendarView
import com.cmp.pushuptracker.ui.components.InfoBottomSheet
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.getRandomQuote
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel

@Composable
fun GetHomeSection(userViewmodel: UserViewmodel, pushupViewModel: PushupViewModel) {
    val pushupDataState by pushupViewModel.pushupData.collectAsState()
    val datePushupMap by rememberUpdatedState(
        newValue = pushupDataState.associateBy { it.date }
    )

    var selectedDate by remember { mutableStateOf<PushUpEntity?>(null) }
    var canShowInfoBottomSheet by remember { mutableStateOf(false) }

    Spacer(Modifier.height(10.dp))
    GetHabitCalendarView(datePushupMap, pushupDataState) {
        selectedDate = it
        canShowInfoBottomSheet = true
    }
    Spacer(Modifier.height(25.dp))
    GetWeeklyGoalsSection(pushupDataState)
    Spacer(Modifier.height(10.dp))
    GetChallengeCard(pushupViewModel)
    Spacer(Modifier.height(25.dp))
    val quote = remember { getRandomQuote() }
    Text(
        "\"$quote\"",
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(15.dp))
    if (selectedDate != null && canShowInfoBottomSheet)
        InfoBottomSheet(selectedDate) {
            canShowInfoBottomSheet = false
        }
}

@Composable
fun GetPersonalBestCard(userViewmodel: UserViewmodel) {

    val userData = userViewmodel.userData

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.pushup_best),
            contentDescription = "Background",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ) {
            // Animated reps count
            Text(
                text = "Personal Best",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            // Formatted duration text
            Text(
                text = "${userData.best} Push-ups",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun GetChallengeCard(pushupViewModel: PushupViewModel) {
    val todayPushup by pushupViewModel.todayData.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                )
                .padding(15.dp)
        ) {
            Image(
                painter = painterResource(
                    if ((todayPushup?.reps ?: 0) >= 50)
                        R.drawable.award
                    else
                        R.drawable.trophy_icon
                ),
                colorFilter = if ((todayPushup?.reps ?: 0) > 50) null else ColorFilter.tint(
                    if ((todayPushup?.reps ?: 0) < 50)
                        MaterialTheme.colorScheme.onBackground
                    else
                        MaterialTheme.colorScheme.onBackground
                ),
                contentDescription = "Challenge",
                modifier = Modifier.size(25.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                "Daily Challenge",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Complete 50 push-ups today",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline,
                style = TextStyle(
                    textDecoration = if ((todayPushup?.reps
                            ?: 0) > 50
                    ) TextDecoration.LineThrough else null
                )
            )
        }
    }
}
