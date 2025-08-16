package com.cmp.pushuptracker.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.viewmodel.UtilViewmodel

@Composable
fun GetThemeSection(
    profileNavController: NavHostController,
    utilViewmodel: UtilViewmodel = hiltViewModel<UtilViewmodel>()
) {
    val selectedTheme = utilViewmodel.theme

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    profileNavController.navigate(Screen.ThemeChangeView.route)
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            "Theme",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            selectedTheme.first().uppercase() + selectedTheme.substring(1).lowercase(),
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
fun GetRedirectSection(
    title: String,
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            title,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open"
        )
    }
}