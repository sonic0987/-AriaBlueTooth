package com.norma.abc

import android.app.Application
import com.norma.abc.utils.NotificationManager

open class GlobalApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        NotificationManager.createChannel(this)
    }
}