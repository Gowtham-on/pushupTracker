package com.cmp.pushuptracker.ui.screen.ProfileScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.ui.components.AppBar
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.Theme
import com.cmp.pushuptracker.viewmodel.UtilViewmodel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ThemeScreen(navController: NavHostController, utilViewmodel: UtilViewmodel) {
    Scaffold {
        Column {
            AppBar("Theme") {
                navController.popBackStack()
            }
            Spacer(Modifier.height(15.dp))
            Column (
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ThemeItem(Theme.DARK, utilViewmodel)
                ThemeItem(Theme.LIGHT, utilViewmodel)
                ThemeItem(Theme.SYSTEM, utilViewmodel)
            }
        }
    }
}

@Composable
fun ThemeItem(theme: Theme, utilViewmodel: UtilViewmodel) {
    val title = remember {
        when (theme) {
            Theme.DARK -> "Dark"
            Theme.LIGHT -> "Light"
            else -> "System"
        }
    }

    val selectedTheme = utilViewmodel.theme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        utilViewmodel.setTheme(theme)
                    }
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(5.dp)
                .then(
                    if (selectedTheme == theme.name) {
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(20.dp)
                            )
                    } else {
                        Modifier
                    }
                )

        )
    }
}