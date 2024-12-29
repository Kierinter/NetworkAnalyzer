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
        
        // 检查权限
        if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }

        // 获取SIM卡数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activeModemCount = telephonyManager.activeModemCount
            for (slotIndex in 0 until activeModemCount) {
                val subManager = context.getSystemService(SubscriptionManager::class.java)
                val subInfo = subManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex)
                
                if (subInfo != null) {
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
                }
            }
        }
    } catch (e: Exception) {
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