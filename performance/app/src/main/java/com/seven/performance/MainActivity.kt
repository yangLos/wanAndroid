package com.seven.performance

import android.content.res.Resources.Theme
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Debug.startMethodTracing("dosth")//系统自带工具类
        LauncherTimerUtils.startTrace()
        doSth()
        LauncherTimerUtils.endTrace()
        Debug.stopMethodTracing()

    }

    private fun doSth(){
        Thread.sleep(1000)
    }
}