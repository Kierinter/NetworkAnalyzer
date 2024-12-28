package com.kks3.networkanalyzer

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import android.net.wifi.WifiInfo as AndroidWifiInfo
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import com.thanosfisherman.wifiutils.WifiUtils

// 数据类用于存储WiFi信息
data class WifiInfo(
    val ssid: String,                // WiFi名称
    val bssid: String,              // MAC地址
    val rssi: Int,                  // 信号强度
    val linkSpeed: Int,             // 连接速度
    val frequency: Int,             // 频率
    val networkId: Int,             // 网络ID
    val ipAddress: String,          // IP地址
    val isConnected: Boolean = false ,// 是否已被连接
    val level: Int ,
    val capabilities: String = "",
    val macAddress: String = ""
)

data class MobileInfo(
    val networkOperatorName: String,  // 运营商名称
    val networkType: String,           // 数据类型
    val signalStrength: Int           // 信号强度

)

//TODO:获取WiFi信息
fun wifiInformation(context: Context ){
    val wifiInfo = getConnectedWifiInfo(context)
    wifiInfo ?.let {info ->
        println("WiFi名称: ${info.ssid}")
        println("信号强度: ${info.rssi} dBm")
        println("信号等级: ${info.level} 级")
        println("连接速度: ${info.linkSpeed} Mbps")
        println("频段: ${getFrequencyBand(info.frequency)}")
        println("IP地址: ${info.ipAddress}")
        println("")
    } ?:println("未连接到WiFi")
}


//TODO #1:获取移动网络信息
fun mobileInformation(context: Context) {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val networkOperatorName = telephonyManager.networkOperatorName
    val networkType = getNetworkTypeString(telephonyManager.networkType)


    println("运营商名称: $networkOperatorName")
    println("数据类型: $networkType")
    println()
}

fun getNetworkTypeString(networkType: Int): String {
    return when (networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
        TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        TelephonyManager.NETWORK_TYPE_NR -> "5G"
        else -> "未知"
    }
}


@SuppressLint("MissingPermission")
fun getConnectedWifiInfo(context: Context): WifiInfo? {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    // 检查 WiFi 是否启用
    if (!wifiManager.isWifiEnabled) {
        return null
    }
    
    // 获取当前连接的网络信息
    val connectionInfo = wifiManager.connectionInfo
    if (connectionInfo != null && connectionInfo.networkId != -1) {
        return WifiInfo(
            ssid = connectionInfo.ssid.removeSurrounding("\""),  // 移除SSID两端的引号
            bssid = connectionInfo.bssid,
            rssi = connectionInfo.rssi,
            linkSpeed = connectionInfo.linkSpeed,
            frequency = connectionInfo.frequency,
            networkId = connectionInfo.networkId,
            ipAddress = formatIpAddress(connectionInfo.ipAddress),
            level = getSignalLevel(connectionInfo.rssi),
            MacAdrress = connectionInfo.macAddress
        )
    }
    return null
}


// 格式化IP地址
private fun formatIpAddress(ipAddress: Int): String {
    return String.format(
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

