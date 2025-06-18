package com.seven.performance

import android.util.Log
import java.util.Date

/**
 *
 * Time: 2025/6/18
 *
 * Author: seven
 *
 * Description: 时间记录工具类
 *
 */

object LauncherTimerUtils {

    private var time: Long = 0

    fun startTrace() {
        time = System.currentTimeMillis();
    }

    fun endTrace() {
        Log.d("Trace", "costTime:${System.currentTimeMillis() - time}")
    }

}