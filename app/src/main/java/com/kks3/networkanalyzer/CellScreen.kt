package com.kks3.networkanalyzer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun Cell(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cellInfo = remember { mutableStateOf(mobileInformation(context)) }  // 使用 mutableStateOf 存储网络信息
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while(true) {
            cellInfo.value = mobileInformation(context)
            outputCellInfo(cellInfo.value)
            delay(3000) // 每3秒更新一次
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 信号强度卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "信号强度",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${cellInfo.value?.signalStrength ?: "未知"} dBm",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = cellInfo.value?.let { cellSignalLevel(it.signalStrength) } ?: "未知",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // 网络信息卡片
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
                    text = "网络信息",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                InfoRow(label = "运营商", value = cellInfo.value?.networkOperatorName ?: "未知")
                InfoRow(label = "网络类型", value = cellInfo.value?.networkType ?: "未知")
                InfoRow(label = "基站ID", value = cellInfo.value?.baseStationId ?: "未知")
                InfoRow(label = "频率", value = cellInfo.value?.frequency ?: "未知")
                InfoRow(label = "电话号码", value = cellInfo.value?.phoneNumber ?: "未知")
            }
        }

        // 在适当的位置显示加载和错误状态
        if (isLoading) {
            CircularProgressIndicator()
        }
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
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

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun CellPreview() {
    MaterialTheme {
        Cell()
    }
}

