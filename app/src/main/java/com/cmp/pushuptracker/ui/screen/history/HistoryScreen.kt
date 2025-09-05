package com.cmp.pushuptracker.ui.screen.history

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.components.InfoBottomSheet
import com.cmp.pushuptracker.viewmodel.PushupViewModel

@Composable
fun HistoryScreen(
    navController: NavHostController,
    pushupViewModel: PushupViewModel,
) {
    val pushupDataState by pushupViewModel.pushupData.collectAsState()

    var selectedDate by rememberSaveable { mutableStateOf<PushUpEntity?>(null) }
    var canShowInfoBottomSheet by rememberSaveable { mutableStateOf(false) }

    val datePushupMap by rememberUpdatedState(
        newValue = pushupDataState.associateBy { it.date }
    )

    Column {
        AppBar("Statistics", false) {
            navController.popBackStack()
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            GetHeatMap(datePushupMap) {
                selectedDate = it
                canShowInfoBottomSheet = true
            }
            Spacer(Modifier.height(16.dp))
            GetRepsChart(datePushupMap)
            Spacer(Modifier.height(16.dp))
            GetDurationChart(datePushupMap)
            Spacer(Modifier.height(16.dp))
            if (selectedDate != null && canShowInfoBottomSheet)
                InfoBottomSheet(selectedDate) {
                    canShowInfoBottomSheet = false
                }
        }
    }

}
