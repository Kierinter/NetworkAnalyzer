package com.kks3.networkanalyzer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

class BooleanPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values : Sequence<Boolean> = sequenceOf(true,false)
}

@Preview(showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun SettingPreview(
    @PreviewParameter(BooleanPreviewParameterProvider::class) isDarkTheme:Boolean
){
    Setting(
        isDarkTheme = isDarkTheme,
        onThemeChange = {}
    )
}

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    isDarkTheme:Boolean,
    onThemeChange:(Boolean)->Unit
) {
    var isDarkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 主题切换卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "深色模式",
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.material3.Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }

        // 软件介绍卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "关于应用",
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                Text(
                    text = "Network Analyzer 是一款网络分析工具，帮助用户监测和分析移动网络与WiFi网络状态。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "版本：1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
