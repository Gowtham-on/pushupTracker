package com.cmp.pushuptracker.ui.screen.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.components.BarGraph
import com.cmp.pushuptracker.ui.components.ExpandingFAB
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PushupIllustrations
import com.cmp.pushuptracker.utils.TimeUtils
import com.cmp.pushuptracker.utils.calculateWeeklyCount
import com.cmp.pushuptracker.utils.estimatePushupCalories
import com.cmp.pushuptracker.utils.getWeeklyReps
import com.cmp.pushuptracker.viewmodel.PushupViewModel
import com.cmp.pushuptracker.viewmodel.UserViewmodel
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavHostController,
    pushupViewModel: PushupViewModel = hiltViewModel<PushupViewModel>(),
    userViewmodel: UserViewmodel
) {
    val pushupData = pushupViewModel.todayData
    val pushups by pushupViewModel.pushupData.collectAsState(initial = emptyList())

    var showQuickAddShet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExpandingFAB(navController) {
                showQuickAddShet = true
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        Box {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
                    .padding(horizontal = 16.dp)
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
                            (pushupData?.reps ?: 0).toString(),
                            "Push-Ups",
                            illustrationType = PushupIllustrations.ONE
                        )
                        GetHomePushupCard(
                            "Time",
                            (TimeUtils.getMinsSecFromSeconds(
                                pushupData?.duration?.toLong() ?: 0L
                            )).toString(),
                            "Workout duration",
                            illustrationType = PushupIllustrations.TWO
                        )
                        GetHomePushupCard(
                            "Calories",
                            estimatePushupCalories(
                                reps = pushupData?.reps ?: 0,
                                durationSec = pushupData?.duration ?: 0,
                                weightKg = 70.0,
                            ),
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
                            calculateWeeklyCount(pushups).toString(),
                            fontFamily = workSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (pushups.isNotEmpty())
                        BarGraph(
                            counts = getWeeklyReps(pushups),
                            barColor = MaterialTheme.colorScheme.primary,
                        )
                    Spacer(Modifier.height(12.dp))
                    if (showQuickAddShet)
                        GetQuickAddSheet(pushupViewModel, userViewmodel) {
                            showQuickAddShet = false
                        }
                }
            }
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
            Spacer(Modifier.height(4.dp))
            Text(
                count,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetQuickAddSheet(
    pushupViewModel: PushupViewModel,
    userViewmodel: UserViewmodel,
    onDismiss: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var secs by remember { mutableStateOf("") }

    val pushups by pushupViewModel.pushupData.collectAsState(initial = emptyList())

    // 2) derive the single record for the date
    val pushupData by remember(pushups, selectedDate) {
        derivedStateOf {
            pushups.firstOrNull { it.date == selectedDate }
        }
    }

    val userData = userViewmodel.userData

    Column {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    "Quick add your progress", fontFamily = workSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.padding(vertical = 10.dp))
                GetSheetTextField(title = "Reps", onTextChange = { reps = it })
                Spacer(Modifier.padding(vertical = 10.dp))
                GetSheetTextField(title = "Sets", onTextChange = { sets = it })
                Spacer(Modifier.padding(vertical = 10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    GetSheetTextField(
                        modifier = Modifier.weight(1f),
                        "Min",
                        onTextChange = { min = it },
                        R.drawable.timer_two
                    )
                    GetSheetTextField(
                        modifier = Modifier.weight(1f),
                        "Seconds",
                        onTextChange = { secs = it },
                        R.drawable.timer_two
                    )
                }
                Spacer(Modifier.padding(vertical = 10.dp))
                GetDatePickerField(selectedDate) {
                    showDatePicker = true
                }
                Spacer(Modifier.padding(vertical = 10.dp))
                Button(
                    onClick = {
                        if (reps.isNotBlank()
                            && sets.isNotBlank()
                            && min.isNotBlank()
                            && secs.isNotBlank()
                            && selectedDate.isNotBlank()
                        ) {
                            if (pushupData != null) {
                                var todayChanges = abs(pushupData!!.reps - reps.toInt())
                                val user = userData
                                user.totalReps += todayChanges.toInt()
                                if (user.best < todayChanges.toInt() == true) {
                                    user.best = todayChanges.toInt()
                                }
                                userViewmodel.updateUserData(userData)
                            }
                            pushupViewModel.addPushupRecord(
                                reps = reps.toInt(),
                                sets = sets.toInt(),
                                duration = (min.toInt() * 60) + secs.toInt(),
                                date = selectedDate
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Save",
                        fontFamily = workSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                    )
                }
                Spacer(Modifier.padding(vertical = 10.dp))
            }
            if (showDatePicker)
                QuickAddCalendar(
                    onSubmit = {
                        selectedDate = TimeUtils.formatTimestamp(it, "dd/MM/yyyy")
                    }
                ) {
                    showDatePicker = false
                }
        }
    }
}

@Composable
fun GetSheetTextField(
    modifier: Modifier = Modifier,
    title: String,
    onTextChange: (String) -> Unit,
    trailingIcons: Int? = null,
) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.matches(Regex("\\d*"))) {
                text = it
                onTextChange(it)
            }
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Text(
                title,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline
            )
        },
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            if (trailingIcons == null) return@OutlinedTextField
            Image(
                painter = painterResource(trailingIcons),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
            )
        }
    )
}


@Composable
fun GetDatePickerField(selectedDate: String, onClick: () -> Unit = {}) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.5f),
                width = 1.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(15.dp)
    ) {
        Text(
            if (selectedDate.isNotBlank()) selectedDate else "Date",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Image(
            painter = painterResource(R.drawable.date),
            contentDescription = "Date",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuickAddCalendar(
    onSubmit: (date: Long) -> Unit,
    closeSelection: UseCaseState.() -> Unit
) {

    val selectedDate = remember { mutableStateOf<List<LocalDate>>(listOf()) }
    val context = LocalContext.current
    CalendarDialog(
        state = rememberUseCaseState(
            visible = true,
            onCloseRequest = { closeSelection() },
            onDismissRequest = {},
            onFinishedRequest = {
                if (selectedDate.value.size == 1) {
                    val date: LocalDate = selectedDate.value.first()
                    val epochMillis: Long = date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    onSubmit(epochMillis)
                } else if (selectedDate.value.size > 1) {
                    Toast.makeText(context, "Select only one Date", Toast.LENGTH_SHORT).show()
                    return@rememberUseCaseState
                }
            }),
        config = CalendarConfig(
            locale = Locale.getDefault(),
            yearSelection = false,
            monthSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Dates { newDates ->
            selectedDate.value = newDates
        },
    )
}