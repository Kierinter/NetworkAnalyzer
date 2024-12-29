package com.kks3.networkanalyzer

import android.webkit.PermissionRequest
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
){
    var permissionGranted by remember { mutableStateOf(false) }

    if(!permissionGranted){
        PermissionHandler(
            onPermissionsGranted = { permissionGranted = true }
        )
    }

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Mobile", "WiFi", "Settings")
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                "Mobile" -> Icon(painter = painterResource(id = R.drawable.mobile_phone_smartphone_icon ), contentDescription = null)
                                "WiFi" -> Icon(painter = painterResource(id = R.drawable.wifi_icon), contentDescription = null)
                                "Settings" -> Icon(Icons.Filled.Settings, contentDescription = null)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> Cell(Modifier.padding(innerPadding))
            1 -> if(permissionGranted) WiFi(Modifier.padding(innerPadding))
            2 -> Setting(
                Modifier.padding(innerPadding),
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}
