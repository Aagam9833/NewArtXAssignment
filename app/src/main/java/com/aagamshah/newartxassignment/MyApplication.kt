package com.aagamshah.newartxassignment

import android.app.Application
import com.aagamshah.newartxassignment.data.remote.AppContainer


class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}