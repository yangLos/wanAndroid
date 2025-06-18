package com.seven.multidex

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.multidex.MultiDex
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

class LoadMultiDexActivity :Activity() {
    private val TAG = "MultiDex_LoadMultiDexActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_multi_dex)
        showLoadingDialog()

        Thread(){
            loadMultiDex()
        }.start()
    }


    @SuppressLint("LongLogTag")
    private fun loadMultiDex() {
        Log.d(TAG, "MultiDex.install 开始: ")
        val startTime = System.currentTimeMillis()
        MultiDex.install(this@LoadMultiDexActivity)
        Log.d(TAG, "MultiDex.install 结束，耗时: " + (System.currentTimeMillis() - startTime))
        try {
            //模拟MultiDex耗时很久的情况
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        afterMultiDex()
    }

    @SuppressLint("LongLogTag")
    private fun afterMultiDex() {
        deleteTempFile(this)
        //将这个进程杀死
        Log.d(TAG, "afterMultiDex: ")
        finish()
        Process.killProcess(Process.myPid())
    }

    @SuppressLint("LongLogTag")
    private fun deleteTempFile(context: Context) {
        try {
            val file = File(context.cacheDir.absolutePath, "load_dex.tmp")
            if (file.exists()) {
                file.delete()
                Log.d(TAG, "deleteTempFile: ")
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }
    private fun showLoadingDialog() {
        AlertDialog.Builder(this)
            .setMessage("加载中，请稍后...")
            .show()
    }
}