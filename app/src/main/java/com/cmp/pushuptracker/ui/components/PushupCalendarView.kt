package com.cmp.pushuptracker.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.TimeUtils
import com.cmp.pushuptracker.utils.vibrate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.shadow.Shadow

@Composable
fun GetHabitCalendarView(
    pushupMap: Map<String, PushUpEntity>,
    list: List<PushUpEntity>,
    onClick: (PushUpEntity?) -> Unit
) {
    var streak by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(list) {
        var currentStreak = 0
        var missedDays = 0

        val today = LocalDate.now()
        val completedDates = list
            .filter { it.reps > 0 }
            .mapNotNull { TimeUtils.parseAppDate(it.date) }
            .toSet()

        var dateToCheck = today

        while (missedDays < 4) {
            if (completedDates.contains(dateToCheck)) {
                currentStreak++
                missedDays = 0 // reset missed streak
            } else {
                missedDays++
            }
            dateToCheck = dateToCheck.minusDays(1)
        }

        streak = currentStreak
    }

    val days = remember {
        TimeUtils.getDateRangeLastSundayToThisSaturday(TimeUtils.STORAGE_DATE_PATTERN, 13)
    }
    val daysList = remember {
        listOf(
            "Su",
            "Mo",
            "Tu",
            "We",
            "Th",
            "Fr",
            "Sa",
            "Su",
            "Mo",
            "Tu",
            "We",
            "Th",
            "Fr",
            "Sa"
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
//        elevation = CardDefaults.cardElevation(
//            defaultElevation = 10.dp
//        ),
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(10.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    spread = 2.dp,
                    alpha = 1f,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )
            .fillMaxWidth()
    ) {
        Column {
            Box {
                Text(
                    TimeUtils.getCurrentMonthYear(),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )

                Text(
                    "Streak: $streak",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterEnd)
                        .padding(end = 20.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                days.take(7).mapIndexed { index, it ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GetDayText(daysList[index])
                        Spacer(Modifier.height(12.dp))
                        GetCircularBg(it, pushupMap, onClick)
                        Spacer(Modifier.height(12.dp))
                        GetCircularBg(
                            days[index + 7].toString(),
                            pushupMap,
                            onClick
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            Spacer(Modifier.height(6.dp))

        }

    }
}

@Composable
fun GetDayText(day: String) {
    Text(
        day,
        fontFamily = workSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun GetCircularBg(
    date: String,
    pushupLogs: Map<String, PushUpEntity>,
    onClick: (PushUpEntity?) -> Unit
) {
    val context = LocalContext.current
    val isCompleted = remember(date, pushupLogs) {
        val reps = pushupLogs[TimeUtils.toStorageDate(date)]?.reps ?: 0
        val complete = reps > 0
        complete
    }
    val infiniteTransition = rememberInfiniteTransition()
    val alphaValue by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(35.dp)
            .clickable(
                onClick = {
                    if (isCompleted) {
                        onClick(pushupLogs[TimeUtils.toStorageDate(date)])
                        vibrate(context)
                    }
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .then(
                if (TimeUtils.isToday(date)
                ) {
                    Modifier
                        .border(
                            width = 3.dp,
                            color = Color(0xFFF4B65C).copy(alpha = alphaValue),
                            shape = CircleShape
                        )
                        .padding(all = 3.dp)
                        .background(
                            shape = CircleShape,
                            color = Color(0xFFF4B65C)
                        )

                } else {
                    Modifier.background(
                        color = if (isCompleted == true)
                            MaterialTheme.colorScheme.primary
                        else if (TimeUtils.isToday(date)) Color.Red
                        else Color.Gray,
                        shape = CircleShape
                    )
                }
            )
    ) {
        Text(
            TimeUtils.dayOfMonthLabel(date),
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.Center)
        )
    }
}
