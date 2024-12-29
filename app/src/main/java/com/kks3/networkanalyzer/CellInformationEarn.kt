package com.kks3.networkanalyzer

import android.content.Context
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.TelephonyManager

data class CellInfo(
    val networkOperatorName: String,  // 运营商名称
    val networkType: String,           // 数据类型
    val signalStrength: Int,           // 信号强度
    val baseStationId: String,            // 基站ID
    val phoneNumber: String,           // 电话号码
    val frequency: String,             // 频率


)

//TODO :获取移动网络信息
fun mobileInformation(context: Context):CellInfo ?{
    try {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        // 检查权限
        if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return null
        }
        
        // 获取所有小区信息
        val cellInfoList = telephonyManager.allCellInfo
        if (cellInfoList.isNullOrEmpty()) return null
        
        // 获取当前活跃的小区信息
        val activeCellInfo = cellInfoList[0]
        
        // 解析基站ID和频率
        val baseStationId = when (activeCellInfo) {
            is CellInfoLte -> {
                val cellIdentity = activeCellInfo.cellIdentity
                "eNB: ${cellIdentity.ci}"
            }
            is CellInfoNr -> {
                val cellIdentity = activeCellInfo.cellIdentity
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    "NR: ${(cellIdentity as android.telephony.CellIdentityNr).nci}"
                } else {
                    "不支持的Android版本"
                }
            }
            else -> "未知"
        }
        
        // 获取频率信息
        val frequency = when (activeCellInfo) {
            is CellInfoLte -> {
                val cellIdentity = activeCellInfo.cellIdentity
                "${cellIdentity.earfcn} MHz"
            }
            is CellInfoNr -> {
                val cellIdentity = activeCellInfo.cellIdentity
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    "${(cellIdentity as android.telephony.CellIdentityNr).nrarfcn} MHz"
                } else {
                    "不支持的Android版本"
                }
            }
            else -> "未知"
        }
        
        // 获取运营商名称
        val operatorName = telephonyManager.networkOperatorName ?: "未知"
        
        // 获取网络类型
        val networkType = when(telephonyManager.dataNetworkType) {
            TelephonyManager.NETWORK_TYPE_LTE -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
            TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
            else -> "未知"
        }
        
        // 获取信号强度（这里使用一个默认值，实际应该通过SignalStrength获取）
        val signalStrength = -85  // 这里需要通过其他方式获取实际的信号强度
        
        return CellInfo(
            networkOperatorName = operatorName,
            networkType = networkType,
            signalStrength = signalStrength,
            baseStationId = baseStationId,
            phoneNumber = telephonyManager.line1Number ?: "未知",
            frequency = frequency
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


//TODO :信号强度分级
fun cellSignalLevel(signalStrength: Int): String {
    if(signalStrength <0) {
        when (signalStrength) {
            in -200..-116 -> return "信号弱"
            in -115..-91 -> return "信号较好"
            in -90..-65 -> return "信号强"
            else -> return "无信号"
        }
    }
    return "error: 信号强度分级失败"
}

// 打印移动网络信息
fun  outputCellInfo(cellInfo: CellInfo?) {
    if (cellInfo == null) {
        println("未获取到移动网络信息")
        return
    }
    println("运营商：${cellInfo.networkOperatorName}")
    println("网络类型：${cellInfo.networkType}")
    println("信号强度：${cellInfo.signalStrength}")
    println("基站ID：${cellInfo.baseStationId}")
    println("电话号码：${cellInfo.phoneNumber}")
    println("频率：${cellInfo.frequency}")
}