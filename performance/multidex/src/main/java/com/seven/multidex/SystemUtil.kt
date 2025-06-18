package com.seven.multidex

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * Time: 2025/6/18
 *
 * Author: seven
 *
 * Description:
 *
 */

object SystemUtil {

    fun getProcessName(context: Context): String? {
        val activityManager: ActivityManager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses: List<ActivityManager.RunningAppProcessInfo> = activityManager
            .getRunningAppProcesses()
        val myPid = Process.myPid()
        if (appProcesses == null || appProcesses.size === 0) {
            return null
        }
        for (appProcess in appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.pid === myPid) {
                    return appProcess.processName
                }
            }
        }
        return null
    }


    fun isVMMultidexCapable(): Boolean {
        return isVMMultidexCapable(System.getProperty("java.vm.version"))
    }

    //MultiDex 拷出来的的方法，判断VM是否支持多dex
    fun isVMMultidexCapable(versionString: String?): Boolean {
        var isMultidexCapable = false
        if (versionString != null) {
            val matcher: Matcher =
                Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
            if (matcher.matches()) {
                try {
                    val major: Int = matcher.group(1).toInt()
                    val minor: Int = matcher.group(2).toInt()
                    isMultidexCapable = major > 2 || major == 2 && minor >= 1
                } catch (var5: NumberFormatException) {
                }
            }
        }
        Log.i(
            "MultiDex_SystemUtil",
            "VM with version " + versionString + if (isMultidexCapable) " has multidex support" else " does not have multidex support"
        )
        return isMultidexCapable
    }
}