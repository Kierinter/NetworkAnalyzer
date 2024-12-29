package com.kks3.networkanalyzer

import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi2Bar
import androidx.compose.material.icons.filled.SignalWifiStatusbarNull
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay



@Composable
fun ConnectedWifiCard(wifiInfo: WifiInfo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "当前连接",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (wifiInfo != null) {
                WifiInfoDetails(wifiInfo)
            } else {
                Text("未连接到WiFi网络")
            }
        }
    }
}

@Composable
fun WifiCard(wifiInfo: WifiInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            WifiInfoDetails(wifiInfo)
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun WifiInfoDetailsPreview(

){
    WifiInfoDetails(
        wifiInfo = WifiInfo(
            ssid = "MyWiFi",
            rssi = -50,
            level = 4,
            frequency = 2437,
            wifiChannel = "6",
            capabilities = "WPA2 PSK",
            isConnected = true,
            ipAddress = "192.168.123.1",
            linkSpeed = 100,
            networkId = 1,
            bssid = "00:11:22:33:44:55",
            macAddress = "00:11:22:33:44:55"
        )
    )
}
@Composable
fun WifiInfoDetails(wifiInfo: WifiInfo) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = wifiInfo.ssid,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = when(wifiInfo.level){
                    4,3 -> Icons.Filled.NetworkWifi
                    2 -> Icons.Filled.NetworkWifi2Bar
                    else -> Icons.Filled.SignalWifiStatusbarNull
                },
                contentDescription = "WiFi level icon",
                tint = when (wifiInfo.level) {
                    4, 3 -> MaterialTheme.colorScheme.primary
                    2 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                },
            )
        }
        Text("信号强度: ${wifiInfo.rssi} dBm (${wifiInfo.level}/4)")
        Text("频率: ${wifiInfo.frequency} MHz (信道 ${wifiInfo.wifiChannel})")
        if (wifiInfo.isConnected) {
            Text("IP地址: ${wifiInfo.ipAddress}")
            Text("连接速度: ${wifiInfo.linkSpeed} Mbps")
        } else{
            Text("安全类型: ${wifiInfo.capabilities}")
        }
    }
}
@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun WiFi(modifier: Modifier = Modifier) {
    var wifiList by remember { mutableStateOf<List<WifiInfo>>(emptyList()) }
    var connectedWifi by remember { mutableStateOf<WifiInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember {mutableStateOf<String?>(null)}
    val context = LocalContext.current

    // 定期刷新WiFi信息
    LaunchedEffect(Unit) {
        while (true) {
            connectedWifi = getConnectedWifiInfo(context)
            wifiList = scanWifiInfo(context).sortedByDescending { it.level }
            isLoading = false
            wifiInformation(context)
            delay(3000) // 每3秒刷新一次
        }
    }
    Log.d("WiFi", "isLoading: $isLoading")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            error != null -> Text(text = "获取WiFi信息失败: ${error}")
            isLoading -> Text(text = "正在获取WiFi信息...")
            else -> {
                // 显示当前连接的WiFi信息
                ConnectedWifiCard(connectedWifi)

                Spacer(modifier = Modifier.height(16.dp))

                // 显示扫描到的WiFi列表
                Text(
                    text = "附近的WiFi网络",
                    style = MaterialTheme.typography.titleLarge
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wifiList) { wifi ->
                        WifiCard(wifi)
                    }

                }
            }
        }
    }
}
