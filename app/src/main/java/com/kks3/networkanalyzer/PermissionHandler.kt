package com.kks3.networkanalyzer

import android.Manifest
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionsGranted: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PRECISE_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG
        )
    )

    val backgroundLocationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    LaunchedEffect(Unit) {

        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    if (!permissionsState.allPermissionsGranted && showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("需要权限") },
            text = {
                Text(
                    "为了获取完整的网络和设备信息，应用需要以下权限：\n" +
                    "• 位置信息\n" +
                    "• WiFi状态\n" +
                    "• 电话状态\n" +
                    "• 通话记录\n" +
                    "• 短信\n" +
                    "\n请在接下来的弹窗中允许这些权限。"
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false  // 关闭对话框
                    permissionsState.launchMultiplePermissionRequest()
                }) {
                    Text("请求权限")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
        !backgroundLocationPermissionState.status.isGranted && 
        permissionsState.allPermissionsGranted && 
        showDialog
    ) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("需要后台位置权限") },
            text = {
                Text("为了在后台持续监测网络状态，需要后台位置权限。")
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false  // 关闭对话框
                    backgroundLocationPermissionState.launchPermissionRequest()
                }) {
                    Text("请求后台位置权限")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    } else if (permissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
    }
}