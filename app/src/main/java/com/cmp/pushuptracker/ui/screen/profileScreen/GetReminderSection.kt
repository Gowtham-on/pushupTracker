package com.cmp.pushuptracker.ui.screen.profileScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.pushuptracker.notifications.DailyReminderScheduler
import com.cmp.pushuptracker.ui.components.ToggleSwitch
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.utils.ReminderTime
import com.cmp.pushuptracker.utils.ReminderType

@Composable
fun GetReminderSection() {
    val context = LocalContext.current
    var morningEnabled by remember {
        mutableStateOf(PreferenceUtil.isReminderEnabled(context, ReminderType.MORNING))
    }
    var eveningEnabled by remember {
        mutableStateOf(PreferenceUtil.isReminderEnabled(context, ReminderType.EVENING))
    }
    var morningTime by remember {
        mutableStateOf(PreferenceUtil.getReminderTime(context, ReminderType.MORNING))
    }
    var eveningTime by remember {
        mutableStateOf(PreferenceUtil.getReminderTime(context, ReminderType.EVENING))
    }
    var editingType by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Reminders",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        ReminderToggleRow(
            label = "Morning reminder",
            time = morningTime,
            enabled = morningEnabled,
            onEnabledChange = {
                morningEnabled = it
                PreferenceUtil.setReminderEnabled(context, ReminderType.MORNING, it)
                DailyReminderScheduler.scheduleReminder(context, ReminderType.MORNING)
            },
            onTimeClick = { editingType = ReminderType.MORNING }
        )
        ReminderToggleRow(
            label = "Evening reminder",
            time = eveningTime,
            enabled = eveningEnabled,
            onEnabledChange = {
                eveningEnabled = it
                PreferenceUtil.setReminderEnabled(context, ReminderType.EVENING, it)
                DailyReminderScheduler.scheduleReminder(context, ReminderType.EVENING)
            },
            onTimeClick = { editingType = ReminderType.EVENING }
        )
    }

    val currentEditingType = editingType
    if (currentEditingType != null) {
        val initialTime = if (currentEditingType == ReminderType.MORNING) morningTime else eveningTime
        ReminderTimeDialog(
            initialTime = initialTime,
            onDismiss = { editingType = null },
            onSave = { time ->
                PreferenceUtil.setReminderTime(context, currentEditingType, time)
                PreferenceUtil.setReminderEnabled(context, currentEditingType, true)
                DailyReminderScheduler.scheduleReminder(context, currentEditingType)
                if (currentEditingType == ReminderType.MORNING) {
                    morningTime = time
                    morningEnabled = true
                } else {
                    eveningTime = time
                    eveningEnabled = true
                }
                editingType = null
            }
        )
    }
}

@Composable
private fun ReminderToggleRow(
    label: String,
    time: ReminderTime,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onTimeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            TextButton(
                onClick = onTimeClick,
                enabled = enabled,
                modifier = Modifier.padding(0.dp)
            ) {
                Text(time.displayText())
            }
        }
        Spacer(Modifier.width(24.dp))
        ToggleSwitch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimeDialog(
    initialTime: ReminderTime,
    onDismiss: () -> Unit,
    onSave: (ReminderTime) -> Unit
) {
    var hourText by remember { mutableStateOf(initialTime.hour.toString()) }
    var minuteText by remember { mutableStateOf(initialTime.minute.toString()) }
    val hour = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()
    val isValid = hour in 0..23 && minute in 0..59

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Reminder time",
                fontFamily = workSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = hourText,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) hourText = it },
                    label = { Text("Hour") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minuteText,
                    onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) minuteText = it },
                    label = { Text("Minute") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onSave(ReminderTime(hour ?: initialTime.hour, minute ?: initialTime.minute))
                    },
                    enabled = isValid
                ) {
                    Text("Save")
                }
            }
        }
    }
}
