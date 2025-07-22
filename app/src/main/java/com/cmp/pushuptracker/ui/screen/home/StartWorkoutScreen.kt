package com.cmp.pushuptracker.ui.screen.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.components.RestIntervalSlider
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.LivePreviewActivity
import com.cmp.pushuptracker.ui.theme.workSansFamily

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StartWorkoutScreen(navController: NavHostController) {
    var context = LocalContext.current
    var interval by remember { mutableIntStateOf(0) }
    var sets by remember { mutableIntStateOf(0) }
    var reps by remember { mutableIntStateOf(0) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            AppBar(
                "Push-up Workout",
                onBackPress = { navController.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                listOf("Sets", "Reps").map {
                    GetFormFields(it) { value, type ->
                        if (type == "Sets") {
                            sets = if (value.isEmpty()) 0
                            else value.toInt()
                        } else {
                            reps = if (value.isEmpty()) 0
                            else value.toInt()
                        }
                    }
                }
                GetSliderField {
                    interval = it
                }
                Spacer(Modifier.height(25.dp))
                GetInfoCard(reps * sets, interval * (sets - 1))
                Spacer(Modifier.height(25.dp))
                GetPrimaryButton(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val intent = Intent(context, LivePreviewActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun GetFormFields(title: String, value: (value: String, type: String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column {
        Text(
            title,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = text,
            onValueChange = {
                if (it.matches(Regex("\\d*"))) {
                    text = it
                    value(it, title)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(
                    "Enter $title",
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            shape = RoundedCornerShape(12.dp)

        )
        Spacer(Modifier.height(15.dp))
    }
}

@Composable
fun GetSliderField(interval: (interval: Int) -> Unit) {
    var sliderValue by remember { mutableIntStateOf(30) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Rest between each sets",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "${sliderValue}s",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    RestIntervalSlider(sliderValue, {
        sliderValue = it
        interval(it)
    }, 0..60)

}

@Composable
fun GetPrimaryButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            "Start",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}


@Composable
fun GetInfoCard(totalReps: Int, interval: Int) {

    val animatedReps by animateIntAsState(
        targetValue = totalReps,
        animationSpec = tween(durationMillis = 1000)
    )

    // Convert interval seconds to "X min Y sec"
    val minutes = interval / 60
    val seconds = interval % 60
    val durationText = if (totalReps == 0) "0 sec" else buildString {
        if (minutes > 0) append("$minutes min")
        if (minutes > 0 && seconds > 0) append(" ")
        if (seconds > 0) append("$seconds sec")
        if (minutes == 0 && seconds == 0) append("0 sec")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.card_bg),
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
                text = "Total Reps: $animatedReps",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            // Formatted duration text
            Text(
                text = "Duration: $durationText",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}