package com.cmp.pushuptracker.ui.screen.onBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.cmp.pushuptracker.viewmodel.UtilViewmodel

@Composable
fun OnBoardingScreen1(
    utilViewmodel: UtilViewmodel,
    userViewmodel: UserViewmodel,
    onBoardingNavController: NavHostController
) {

    Scaffold { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
                .padding(horizontal = 15.dp)
                .padding(top = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Skip",
                        color = MaterialTheme.colorScheme.outline,
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable(
                                onClick = {
                                    onBoardingNavController.navigate(Screen.OnBoardingTwo.route)
                                }
                            )
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Smarter Push-Ups. Better Results.",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    "On-device AI counts your reps, tracks your sessions, and it's all ready to go without sign-up.",
                    color = MaterialTheme.colorScheme.outline,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                )
                Spacer(Modifier.height(25.dp))
//            Image(
//                painter = painterResource(id = R.drawable.onboarding_img_1),
//                contentDescription = "Onboarding Image 1",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(10.dp))
//                    .height(300.dp),
//
//                )
//            Spacer(Modifier.height(20.dp))

                GetFeatureSection(
                    "AI Rep Counter",
                    "AI rep counter works offline.",
                    R.drawable.brain,
                )
                Spacer(Modifier.height(25.dp))
                GetFeatureSection(
                    "Weekly Charts & History",
                    "View your progress over time.",
                    R.drawable.chart,
                )
                Spacer(Modifier.height(25.dp))
                GetFeatureSection(
                    "Quick Setup",
                    "Quick setup sets, reps and rest.",
                    R.drawable.time,
                )
                Spacer(Modifier.height(35.dp))
            }
            Button(
                onClick = {
                    onBoardingNavController.navigate(Screen.OnBoardingTwo.route)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Continue",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

        }
    }
}

@Composable
fun GetFeatureSection(heading: String, description: String, icon: Int) {
    Row {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .size(50.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Onboarding Images",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Spacer(Modifier.width(15.dp))
        Column {
            Text(
                heading,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            Text(
                description,
                color = MaterialTheme.colorScheme.outline,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
            )
        }
    }
}