package com.kks3.networkanalyzer

import android.Manifest
import java.util.Locale
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.app.ActivityCompat

// 数据类用于存储WiFi信息
data class WifiInfo(
    val ssid: String,                // WiFi名称
    val bssid: String,               // BSSID
    val wifiChannel: String,        // WiFi信道
    val rssi: Int,                  // 信号强度
    val linkSpeed: Int,             // 连接速度
    val frequency: Int,             // 频率
    val networkId: Int,             // 网络ID
    val ipAddress: String,          // IP地址
    val isConnected: Boolean = false ,// 是否已被连接
    val level: Int ,                   // 信号等级
    val capabilities: String = "",  // 加密方式
    val macAddress: String = ""     // MAC地址
)


fun wifiInformation(context: Context ){
    val wifiInfo = getConnectedWifiInfo(context)
    wifiInfo ?.let {info ->
        println("WiFi名称: ${info.ssid}")
        println("信号强度: ${info.rssi} dBm")
        println("信号等级: ${info.level} 级")
        println("连接速度: ${info.linkSpeed} Mbps")
        println("频段: ${getFrequencyBand(info.frequency)}")
        println("IP地址: ${info.ipAddress}")
        println("安全类型: ${info.capabilities}")
    } ?:println("未连接到WiFi")
}




@SuppressLint("MissingPermission")
fun getConnectedWifiInfo(context: Context): WifiInfo? {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    //检查权限
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return null
    }
    // 检查 WiFi 是否启用
    if (!wifiManager.isWifiEnabled) {
        return null
    }
    
    // 获取当前连接的网络信息
    val connectionInfo = wifiManager.connectionInfo
    if (connectionInfo != null && connectionInfo.networkId != -1) {

        return WifiInfo(
            ssid = connectionInfo.ssid.removeSurrounding("\""),
            bssid = connectionInfo.bssid,
            rssi = connectionInfo.rssi,
            linkSpeed = connectionInfo.linkSpeed,
            frequency = connectionInfo.frequency,
            networkId = connectionInfo.networkId,
            ipAddress = formatIpAddress(connectionInfo.ipAddress),
            level = getSignalLevel(connectionInfo.rssi),
            wifiChannel = getFrequencyBand(connectionInfo.frequency),
            macAddress = connectionInfo.macAddress,
            isConnected = true
        )
    }else return null
}

fun scanWifiInfo(context: Context): List<WifiInfo> {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (!wifiManager.isWifiEnabled) {
        return emptyList()
    }
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return emptyList()
    }
    wifiManager.startScan()
    return wifiManager.scanResults.map { scanResult ->
        WifiInfo(
            ssid = scanResult.SSID,
            bssid = scanResult.BSSID,
            rssi = scanResult.level,
            frequency = scanResult.frequency,
            ipAddress = "",
            level = getSignalLevel(scanResult.level),
            wifiChannel = getFrequencyBand(scanResult.frequency),
            networkId = -1,
            linkSpeed = -1,
            capabilities = scanResult.capabilities
        )
    }
}

// 格式化IP地址
private fun formatIpAddress(ipAddress: Int): String {
    return String.format(
        Locale.US,
        "%d.%d.%d.%d",
        (ipAddress and 0xff),
        (ipAddress shr 8 and 0xff),
        (ipAddress shr 16 and 0xff),
        (ipAddress shr 24 and 0xff)
    )
}

// 获取WiFi信号强度等级
fun getSignalLevel(rssi: Int): Int {
    return when {
        rssi >= -50 -> 4  // 信号很强
        rssi >= -60 -> 3  // 信号强
        rssi >= -70 -> 2  // 信号一般
        rssi >= -80 -> 1  // 信号弱
        else -> 0         // 信号很弱
    }
}

// 获取WiFi频段信息
fun getFrequencyBand(frequency: Int): String {
    return when {
        frequency >= 5000 -> "5 GHz"
        frequency >= 2400 -> "2.4 GHz"
        else -> "未知"
    }
}

// 安全类型
fun getSecurityType(capabilities: String): String {
    return when {
        capabilities.contains("WPA2") -> "WPA2"
        capabilities.contains("WPA") -> "WPA"
        capabilities.contains("WEP") -> "WEP"
        else -> "未知"
    }
}

