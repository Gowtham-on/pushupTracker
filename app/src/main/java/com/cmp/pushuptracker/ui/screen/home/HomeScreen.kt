package com.cmp.pushuptracker.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.components.BarGraph
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PushupIllustrations
import com.cmp.pushuptracker.utils.TimeUtils

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.height(15.dp))
        Text(
            "Push-Up Tracker",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState()),
        ) {
            Text(
                "Today, ${TimeUtils.getTodayDate("MMMM dd")}",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                GetHomePushupCard(
                    "Reps",
                    "120",
                    "Push-Ups",
                    illustrationType = PushupIllustrations.ONE
                )
                GetHomePushupCard(
                    "Time",
                    "15-min",
                    "Workout duration",
                    illustrationType = PushupIllustrations.TWO
                )
                GetHomePushupCard(
                    "Calories",
                    "250",
                    "Estimated Calories burnt",
                    illustrationType = PushupIllustrations.THREE
                )
            }
            Spacer(Modifier.height(24.dp))
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
                    "500",
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            BarGraph(
                listOfCounts = listOf(100, 200, 300, 400, 500, 600, 700),
                barColor = MaterialTheme.colorScheme.primary,
                barLineThickness = 2
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
fun GetHomePushupCard(
    type: String,
    count: String,
    desc: String,
    modifier: Modifier = Modifier,
    illustrationType: PushupIllustrations
) {
    val illustration = remember {
        when (illustrationType) {
            PushupIllustrations.ONE -> R.drawable.pushup_one
            PushupIllustrations.TWO -> R.drawable.pushup_two
            PushupIllustrations.THREE -> R.drawable.pushup_three
        }
    }
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                type,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                count,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                desc,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Image(
            painter = painterResource(illustration),
            contentDescription = "Illustration",
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .height(70.dp)
                .width(130.dp),
            alignment = Alignment.CenterEnd,
            contentScale = ContentScale.Crop
        )
    }

}