package com.kks3.networkanalyzer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellIdentityNr
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log

data class CellInfo(
    val slotIndex: Int,                // 卡槽索引
    val networkOperatorName: String,  // 运营商名称
    val networkType: String,           // 数据类型
    val signalStrength: Int,           // 信号强度
    val baseStationId: String,            // 基站ID
    val phoneNumber: String,           // 电话号码
    val frequency: String,             // 频率
    val isDataEnabled: Boolean         // 是否为数据卡
)

//TODO :获取移动网络信息
fun mobileInformation(context: Context):List<CellInfo> {
    val cellInfoList = mutableListOf<CellInfo>()
    try {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        // 检查所有必需的权限
        val permissions = arrayOf(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_PHONE_NUMBERS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        // 检查每个权限的状态并输出日志
        permissions.forEach { permission ->
            val isGranted = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            Log.d("CellInfo", "Permission $permission: ${if (isGranted) "Granted" else "Denied"}")
        }

        // 如果任何必需的权限未授予，返回空列表
        if (permissions.any { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }) {
            Log.d("CellInfo", "Some permissions are not granted")
            return emptyList()
        }

        // 获取SIM卡数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activeModemCount = telephonyManager.activeModemCount
            Log.d("CellInfo", "Active modem count: $activeModemCount")
            
            for (slotIndex in 0 until activeModemCount) {
                val subManager = context.getSystemService(SubscriptionManager::class.java)
                val subInfo = subManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex)
                
                if (subInfo != null) {
                    Log.d("CellInfo", "Found SIM in slot $slotIndex")
                    val subId = subInfo.subscriptionId
                    val perSimTelephony = telephonyManager.createForSubscriptionId(subId)
                    
                    var signalStrength = -999
                    var baseStationId = "未知"
                    var frequency = "未知"
                    
                    // 获取当前活跃的小区信息
                    val cellInfo = perSimTelephony.allCellInfo
                    if (!cellInfo.isNullOrEmpty()) {
                        val activeCellInfo = cellInfo[0]
                        
                        // 获取信号强度
                        signalStrength = when (activeCellInfo) {
                            is CellInfoLte -> activeCellInfo.cellSignalStrength.dbm
                            is CellInfoNr -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    activeCellInfo.cellSignalStrength.dbm
                                } else {
                                    -999
                                }
                            }
                            else -> -999
                        }
            // 获取基站ID
            baseStationId = when (activeCellInfo) {
                is CellInfoLte -> {
                    val cellIdentity = activeCellInfo.cellIdentity
                    "eNB: ${cellIdentity.ci}"
                }
                is CellInfoNr -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        "NR: ${(activeCellInfo.cellIdentity as CellIdentityNr).nci}"
                    } else {
                        "不支持的Android版本"
                    }
                }
                else -> "未知"
            }

            // 获取频率信息
            frequency = when (activeCellInfo) {
                is CellInfoLte -> {
                    val cellIdentity = activeCellInfo.cellIdentity
                    "${cellIdentity.earfcn} MHz"
                }
                is CellInfoNr -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        "${(activeCellInfo.cellIdentity as CellIdentityNr).nrarfcn} MHz"
                    } else {
                        "不支持的Android版本"
                    }
                }
                else -> "未知"
            }

                    }
                    
                    // 获取运营商名称
                    val operatorName = subInfo.carrierName?.toString() ?: "未知"
                    
                    // 获取网络类型
                    val networkType = when(perSimTelephony.dataNetworkType) {
                        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                        TelephonyManager.NETWORK_TYPE_NR -> "5G"
                        TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
                        TelephonyManager.NETWORK_TYPE_GPRS -> "2G"
                        else -> "未知"
                    }
                    
                    cellInfoList.add(CellInfo(
                        slotIndex = slotIndex + 1,
                        networkOperatorName = operatorName,
                        networkType = networkType,
                        signalStrength = signalStrength,
                        baseStationId = baseStationId,
                        phoneNumber = perSimTelephony.line1Number ?: "未知",
                        frequency = frequency,
                        isDataEnabled = subId == SubscriptionManager.getDefaultDataSubscriptionId()
                    ))
                } else {
                    Log.d("CellInfo", "No SIM found in slot $slotIndex")
                }
            }
        } else {
            Log.d("CellInfo", "Device API level is below Android R")
        }
        
    } catch (e: Exception) {
        Log.e("CellInfo", "Error getting mobile information", e)
        e.printStackTrace()
    }
    return cellInfoList
}


//TODO :信号强度分级
fun cellSignalLevel(signalStrength: Int): String {
    if(signalStrength <0) {
        when (signalStrength) {
            in -120..-91 -> return "信号弱"
            in -90..-71 -> return "信号较好"
            in -70..-50 -> return "信号强"
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