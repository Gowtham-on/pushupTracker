package com.cmp.pushuptracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.theme.workSansFamily

@Composable
fun ExpandingFAB(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(300),
        label = "Fab Rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = {
                        expanded = !expanded
                        navController.navigate(Screen.StartWorkout.route)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Text(
                        "Start Workout",
                        modifier = Modifier.padding(10.dp),
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = {
                        expanded = !expanded
                        navController.navigate(Screen.QuickAdd.route)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        "Quick Add",
                        modifier = Modifier.padding(10.dp),
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    expanded = !expanded
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.rotate(rotation),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}
