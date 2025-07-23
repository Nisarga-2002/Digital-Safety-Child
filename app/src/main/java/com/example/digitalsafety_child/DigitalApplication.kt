package com.example.digitalsafety_child

import android.app.Application
import com.example.digitalsafety_child.utils.SharedPreferences

class DigitalApplication :Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferences.init(applicationContext)
    }
}