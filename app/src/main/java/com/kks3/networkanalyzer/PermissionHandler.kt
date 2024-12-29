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
    permissions: List<String>,
    onPermissionsGranted: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

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
                    "为了获取完整的网络信息，应用需要相关权限。\n" +
                    "请在接下来的弹窗中允许这些权限。"
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
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
    } else if (permissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
    }
}