package com.cmp.pushuptracker.ui.screen.profileScreen


import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cmp.pushuptracker.ui.components.ToggleSwitch
import com.cmp.pushuptracker.ui.theme.workSansFamily
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.utils.openCameraPermissionSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetPermissionSection() {

    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var isNotificationPermissionGranted by remember { mutableStateOf(notificationPermission.status.isGranted) }
    var isCameraPermissionGranted by remember { mutableStateOf(cameraPermission.status.isGranted) }
    val context = LocalContext.current


    var shoulderPref by remember { mutableStateOf(false) }
    var elbowPref by remember { mutableStateOf(false) }
    var hipPref by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shoulderPref = PreferenceUtil.getPushupSettingsPreference(PreferenceUtil.SHOULDER_DETECT, context)
        elbowPref = PreferenceUtil.getPushupSettingsPreference(PreferenceUtil.ELBOW_DETECT, context)
        hipPref = PreferenceUtil.getPushupSettingsPreference(PreferenceUtil.HIP_DETECT, context)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Permissions",
            fontFamily = workSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
        PermissionToggleRow(
            label = "Camera Permission",
            desc = "To analyze pose during workout",
            isGranted = isCameraPermissionGranted,
            onRequest = {
                if (cameraPermission.status.shouldShowRationale)
                    cameraPermission.launchPermissionRequest()
                else openCameraPermissionSettings(
                    context
                )
            },
            openSettings = { openCameraPermissionSettings(context) }
        )
        PermissionToggleRow(
            label = "Detect Shoulder",
            desc = "Mark shoulder position during workout",
            isGranted = shoulderPref,
            onRequest = {
                shoulderPref = !shoulderPref
                PreferenceUtil.savePushupSettingsPreference(
                    PreferenceUtil.SHOULDER_DETECT, context, shoulderPref
                )
            },
            openSettings = { openCameraPermissionSettings(context) }
        )
        PermissionToggleRow(
            label = "Detect Elbow",
            desc = "Mark elbow position during workout",
            isGranted = elbowPref,
            onRequest = {
               elbowPref = !elbowPref
                PreferenceUtil.savePushupSettingsPreference(
                    PreferenceUtil.ELBOW_DETECT, context, elbowPref
                )
            },
            openSettings = { openCameraPermissionSettings(context) }
        )
        PermissionToggleRow(
            label = "Detect Hip",
            desc = "Mark elbow position during workout",
            isGranted = hipPref,
            onRequest = {
               hipPref = !hipPref
                PreferenceUtil.savePushupSettingsPreference(
                    PreferenceUtil.HIP_DETECT, context, hipPref
                )
            },
            openSettings = { openCameraPermissionSettings(context) }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isNotificationPermissionGranted = notificationPermission.status.isGranted
                isCameraPermissionGranted = cameraPermission.status.isGranted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun PermissionToggleRow(
    label: String,
    desc: String = "",
    isGranted: Boolean,
    onRequest: () -> Unit,
    openSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                label,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                desc,
                fontFamily = workSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                style = TextStyle(
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.outline,
            )
        }
        Spacer(Modifier.width(50.dp))
        ToggleSwitch(
            checked = isGranted,
            onCheckedChange = {
                if (!isGranted) onRequest() else openSettings()
            },
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline
        )
    }
}
