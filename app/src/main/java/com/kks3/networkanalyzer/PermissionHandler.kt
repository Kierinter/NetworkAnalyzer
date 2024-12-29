package com.kks3.networkanalyzer

import android.Manifest
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
import androidx.compose.foundation.layout.Column
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissions: List<String>,
    onPermissionsGranted: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions,
        onPermissionsResult = { results ->
            if (results.all { it.value }) {
                onPermissionsGranted()
            }
        }
    )

    // 首次进入时自动请求权限
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        } else {
            onPermissionsGranted()
        }
    }

    if (!permissionsState.allPermissionsGranted && showDialog) {
        AlertDialog(
            onDismissRequest = { /* 不允许通过点击外部关闭对话框 */ },
            title = { Text("需要权限") },
            text = {
                Column {
                    Text("为了获取完整的网络信息，应用需要以下权限：")
                    permissions.forEach { permission ->
                        Text("• ${getPermissionDescription(permission)}")
                    }
                    if (permissionsState.shouldShowRationale) {
                        Text("\n您之前拒绝了某些权限，这些权限对应用功能至关重要。")
                    } else {
                        Text("\n请在接下来的弹窗中允许这些权限。")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text(if (permissionsState.shouldShowRationale) "重新请求权限" else "请求权限")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

private fun getPermissionDescription(permission: String): String {
    return when (permission) {
        Manifest.permission.READ_PHONE_STATE -> "读取手机状态（获取网络信息）"
        Manifest.permission.READ_PHONE_NUMBERS -> "读取电话号码"
        Manifest.permission.ACCESS_FINE_LOCATION -> "精确位置（获取基站和WiFi信息）"
        Manifest.permission.ACCESS_COARSE_LOCATION -> "大致位置（获取基站和WiFi信息）"
        Manifest.permission.ACCESS_WIFI_STATE -> "WiFi状态（获取WiFi信息）"
        Manifest.permission.CHANGE_WIFI_STATE -> "修改WiFi状态（扫描WiFi网络）"
        Manifest.permission.READ_PRECISE_PHONE_STATE -> "精确手机状态（获取详细网络信息）"
        else -> permission.split(".").last()
    }
}