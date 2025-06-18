package com.seven.multidex

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.multidex.MultiDex
import com.seven.multidex.SystemUtil.getProcessName
import com.seven.multidex.SystemUtil.isVMMultidexCapable
import java.io.File


/**
 *
 * Time: 2025/6/18
 *
 * Author: seven
 *
 * Description:
 *
 */

class MyApp : Application() {

    private  val TAG = "MultiDex_MyApp"

    override fun onCreate() {
        super.onCreate()
        if (!isMainProcess(this)) {
            Log.d(TAG, "onCreate: 非主进程，return")
            return
        }
        Log.d(TAG, "主进程 onCreate: 一些初始化操作")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.d(TAG, "attachBaseContext-getPackageName: " + base!!.packageName)
        Log.d(
            TAG, "attachBaseContext-getProcessName: " + getProcessName(
                base!!
            )
        )

        val isMainProcess: Boolean = isMainProcess(base)
        Log.d(TAG, "attachBaseContext-isMainProcess: $isMainProcess")


        //主进程并且vm不支持多dex的情况下才使用 MultiDex
        if (isMainProcess && !isVMMultidexCapable()) {
            loadMultiDex(base)
        }
    }

    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getProcessName(context)
    }

    private fun loadMultiDex(context: Context) {
        newTempFile(context) //创建临时文件
        //启动另一个进程去加载MultiDex
        val intent = Intent(context, LoadMultiDexActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        //检查MultiDex是否安装完（安装完会删除临时文件）
        checkUntilLoadDexSuccess(context)
        //另一个进程以及加载 MultiDex，有缓存了，所以主进程再加载就很快了。
        //为什么主进程要再加载，因为每个进程都有一个ClassLoader
        val startTime = System.currentTimeMillis()
        MultiDex.install(context)
        Log.d(TAG, "第二次 MultiDex.install 结束，耗时: " + (System.currentTimeMillis() - startTime))
        preNewActivity()
    }

    private fun preNewActivity() {
        val startTime = System.currentTimeMillis()
        val mainActivity = MainActivity() //创建实例 这里是为了减少启动时间
        Log.d(TAG, "preNewActivity 耗时: " + (System.currentTimeMillis() - startTime))
    }


    //创建一个临时文件，MultiDex install 成功后删除
    private fun newTempFile(context: Context) {
        try {
            val file = File(context.cacheDir.absolutePath, "load_dex.tmp")
            Log.d(TAG, "try create newTempFile: ")
            if (!file.exists()) {
                Log.d(TAG, "newTempFile: ")
                file.createNewFile()
            }
        } catch (th: Throwable) {
            Log.d(TAG,"newTempFile:"+th.message)
            th.printStackTrace()
        }
    }

    /**
     * 检查MultiDex是否安装完,通过判断临时文件是否被删除
     * @param context
     * @return
     */
    private fun checkUntilLoadDexSuccess(context: Context) {
        val file = File(context.cacheDir.absolutePath, "load_dex.tmp")
        var i = 0
        val waitTime = 100 //睡眠时间
        try {
            Log.d(TAG, "checkUntilLoadDexSuccess: >>> ")
            while (file.exists()) {
                Thread.sleep(waitTime.toLong())
                Log.d(TAG, "checkUntilLoadDexSuccess: sleep count = " + ++i)
                if (i > 40) {
                    Log.d(TAG, "checkUntilLoadDexSuccess: 超时，等待时间： " + waitTime * i)
                    break
                }
            }
            Log.d(TAG, "checkUntilLoadDexSuccess: 轮循结束，等待时间 " + waitTime * i)
        } catch (e: Exception) {
            Log.d(TAG,"checkUntilLoadDex error  "+e.message)
            e.printStackTrace()
        }
    }
}