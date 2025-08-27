package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.ui.theme.workSansFamily

@Composable
fun GetRepsCountView(reps: Int) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(horizontal = 7.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            "Reps Count: $reps",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White,
        )
    }

}