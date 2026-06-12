package com.cmp.pushuptracker.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.GetHabitCalendarView
import com.cmp.pushuptracker.ui.components.InfoBottomSheet
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.getRandomQuote
import com.cmp.pushuptracker.utils.TimeUtils
import com.cmp.pushuptracker.viewmodel.PushupViewModel

@Composable
fun GetHomeSection(
    pushupViewModel: PushupViewModel,
) {
    val pushupDataState by pushupViewModel.pushupData.collectAsState()
    val datePushupMap by rememberUpdatedState(
        newValue = pushupDataState.associateBy { TimeUtils.toStorageDate(it.date) }
    )

    var selectedDate by remember { mutableStateOf<PushUpEntity?>(null) }
    var canShowInfoBottomSheet by remember { mutableStateOf(false) }

    GetHabitCalendarView(datePushupMap, pushupDataState) {
        selectedDate = it
        canShowInfoBottomSheet = true
    }
    Spacer(Modifier.height(10.dp))
    Row (
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Image(
            imageVector = Icons.Default.Info, contentDescription = "Info",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .size(15.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            "Build momentum with 5 active days. Rest shields protect recovery days.",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(Modifier.height(25.dp))
    GetWeeklyGoalsSection(pushupDataState)
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
