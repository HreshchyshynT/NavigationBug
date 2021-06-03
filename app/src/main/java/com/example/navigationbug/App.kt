package com.example.navigationbug

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    private var someValue = true
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getValue(): Boolean {
        val result = someValue
        someValue = !someValue
        return result
    }
}