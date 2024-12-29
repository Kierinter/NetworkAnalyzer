package com.kks3.networkanalyzer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.lang.System.out

@Composable
fun Cell(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cellInfoList = remember { mutableStateOf<List<CellInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while(true) {
            try {
                cellInfoList.value = mobileInformation(context)
                isLoading = false
                error = null
            } catch (e: Exception) {
                error = e.message
            }
            delay(1500)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            error != null -> Text(text = "获取移动网络信息失败: $error")
            isLoading -> Text(text = "正在获取移动网络信息...")
            cellInfoList.value.isEmpty() -> Text(text = "未检测到SIM卡")
            else -> {
                cellInfoList.value.forEach { cellInfo ->
                    CellInfoCard(cellInfo)
                    out.println(cellInfo)
                }
            }
        }
    }
}

@Composable
fun CellInfoCard(cellInfo: CellInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "SIM卡 ${cellInfo.slotIndex} ${if (cellInfo.isDataEnabled) "(数据卡)" else ""}",
                style = MaterialTheme.typography.titleMedium
            )

            // 信号强度显示
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${cellInfo.signalStrength} dBm",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = cellSignalLevel(cellInfo.signalStrength),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Divider()

            // 网络信息
            InfoRow(label = "运营商", value = cellInfo.networkOperatorName)
            InfoRow(label = "网络类型", value = cellInfo.networkType)
            InfoRow(label = "基站ID", value = cellInfo.baseStationId)
            InfoRow(label = "频率", value = cellInfo.frequency)
            InfoRow(label = "电话号码", value = cellInfo.phoneNumber)
        }
    }
}
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

