package com.cmp.pushuptracker.ui.screen.home

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.ui.components.BarGraph
import com.cmp.pushuptracker.ui.components.ExpandingFAB
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PushupIllustrations
import com.cmp.pushuptracker.utils.TimeUtils
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate
import java.time.ZoneId

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {

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
                    if (showQuickAddShet)
                        GetQuickAddSheet(
                            onSubmit = {}
                        ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetQuickAddSheet(onSubmit: () -> Unit, onDismiss: () -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var secs by remember { mutableStateOf("") }

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
                    onClick = {},
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

@RequiresApi(Build.VERSION_CODES.O)
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
            onFinishedRequest = {
                if (selectedDate.value.size == 1) {
                    val date: LocalDate = selectedDate.value.first()
                    val epochMillis: Long = date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    onSubmit(epochMillis)
                } else if (selectedDate.value.size == 1) {
                    Toast.makeText(context, "Select only one Date", Toast.LENGTH_SHORT).show()
                }
            }),
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH,
        ),
        selection = CalendarSelection.Dates { newDates ->
            selectedDate.value = newDates
        },
    )
}