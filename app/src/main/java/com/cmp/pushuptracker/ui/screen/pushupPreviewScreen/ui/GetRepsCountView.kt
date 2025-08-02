package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.ui.theme.workSansFamily

@Composable
fun GetRepsCountView(viewModel: LivePreviewViewmodel) {
    Box (
        modifier = Modifier
            .padding(15.dp)
    ){
        Text(
            "Reps Count: ${viewModel.currentRep}",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White,
        )
    }

}