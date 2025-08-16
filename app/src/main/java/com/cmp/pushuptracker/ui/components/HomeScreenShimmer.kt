package com.cmp.pushuptracker.ui.components// HomeScreenShimmer.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenShimmer() {
    ShimmerContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header row: title (left) + hero image (right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBlock(Modifier.width(220.dp).height(28.dp), shape = RoundedCornerShape(6.dp)) // "Push-Up Tracker"
                    ShimmerBlock(Modifier.width(90.dp).height(14.dp), shape = RoundedCornerShape(6.dp))  // "Calories"
                    ShimmerBlock(Modifier.width(120.dp).height(24.dp), shape = RoundedCornerShape(6.dp)) // 48.08
                    ShimmerBlock(Modifier.width(180.dp).height(14.dp), shape = RoundedCornerShape(6.dp)) // subtitle
                }
                ShimmerBlock(
                    Modifier.size(140.dp),
                    shape = RoundedCornerShape(18.dp) // right illustration card
                )
            }

            // Calendar card
            ShimmerBlock(
                Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(20.dp)
            )

            // "Weekly Goals" title + metric
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerBlock(Modifier.width(160.dp).height(22.dp), shape = RoundedCornerShape(6.dp)) // Weekly Goals
                ShimmerBlock(Modifier.width(80.dp).height(14.dp), shape = RoundedCornerShape(6.dp))  // Push-Ups
                ShimmerBlock(Modifier.width(90.dp).height(26.dp), shape = RoundedCornerShape(6.dp))  // 493
            }

            // Bar chart area (five bars with different heights)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val barShape = RoundedCornerShape(8.dp)
                ShimmerBlock(Modifier.width(48.dp).height(140.dp), barShape)
                ShimmerBlock(Modifier.width(48.dp).height(200.dp), barShape)
                ShimmerBlock(Modifier.width(48.dp).height(160.dp), barShape)
                ShimmerBlock(Modifier.width(48.dp).height(180.dp), barShape)
                ShimmerBlock(Modifier.width(48.dp).height(150.dp), barShape)
            }

            // Spacer to push FAB area
            Spacer(Modifier.weight(1f))

            // FAB placeholder (bottom-right)
            Box(Modifier.fillMaxWidth()) {
                ShimmerCircle(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}
