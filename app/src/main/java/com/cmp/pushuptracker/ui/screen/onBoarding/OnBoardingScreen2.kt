package com.cmp.pushuptracker.ui.screen.onBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.navigationUtils.Screen
import com.cmp.pushuptracker.ui.screen.home.GetSheetTextField
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.cmp.pushuptracker.viewmodel.UtilViewmodel
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen2(
    utilViewmodel: UtilViewmodel,
    userViewmodel: UserViewmodel,
    onBoardingNavController: NavHostController
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var canShowWeightDialog by remember { mutableStateOf(false) }
    var weightText by remember { mutableStateOf("") }

    Scaffold { it ->
        Column(
            modifier = Modifier
                .padding(
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
                .padding(horizontal = 15.dp)
                .padding(top = 5.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier.verticalScroll(state = rememberScrollState())
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "See Your Progress Grow",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    "Review weekly bars, track personal bests, and maintain your streak.",
                    color = MaterialTheme.colorScheme.outline,
                    fontFamily = workSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 17.sp,
                )
                Spacer(Modifier.height(25.dp))
                Image(
                    painter = painterResource(id = R.drawable.bar_chart),
                    contentDescription = "Onboarding Image 1",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .height(200.dp),

                    )
                Spacer(Modifier.height(20.dp))
                GetWorkingSection()
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        canShowWeightDialog = true
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
                if (canShowWeightDialog)
                    GetWeightAlertDialog(
                        weightText = {
                            weightText = it
                        },
                        onSkip = {
                            weightText = "0.0"
                        },
                        onDismissRequest = {
                            canShowWeightDialog = false
                        }
                    ) {
                        scope.launch {
                            userViewmodel.addUser(
                                "user_1002",
                                0,
                                0,
                                weightText.toDoubleOrNull() ?: 0.0
                            )
                            canShowWeightDialog = false
                            PreferenceUtil.completeOnboarding(context)
                            onBoardingNavController.navigate(Screen.Home.route) {
                                popUpTo(Screen.OnBoardingTwo.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetWeightAlertDialog(
    weightText: (String) -> Unit,
    onDismissRequest: () -> Unit = {},
    onSkip: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text(
                "Please enter your weight, that helps us to track calories burned.",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            GetSheetTextField(title = "Enter your weight (in Kg)", onTextChange = weightText)
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .weight(1f)
                ) {
                    Text(
                        "Skip",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .weight(1f)
                ) {
                    Text(
                        "Continue",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

    }
}

@Composable
fun GetWorkingSection() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                "How it works",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            GetTitleDescSection(1, "Pick your plan", "Choose your sets, reps and rest. Easy peasy.")
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            GetTitleDescSection(
                2,
                "Tap Start, hit the floor",
                "We'll count every rep while you crush them."
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            GetTitleDescSection(
                3,
                "Save & celebrate",
                "Tweak if needed, save the set, and keep the streak alive."
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))
            GetTitleDescSection(
                4,
                "Watch the bar climbs",
                "Weekly charts, PB badges and a very smug future you"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun GetTitleDescSection(count: Int, title: String, desc: String) {
    Row {
        Text(
            "$count.",
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                title,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                desc,
                color = MaterialTheme.colorScheme.outline,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
            )
        }
    }
}

@Preview
@Composable
fun GetWorkingSectionPreview() {
    GetWorkingSection()
}