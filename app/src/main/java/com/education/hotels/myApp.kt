package com.education.hotels

import android.app.Application
import java.sql.Connection

class MyApp : Application() {
    var connection: Connection? = null
    override fun onCreate() {
        super.onCreate()
        connect()
    }

    private fun connect() {
        val c = ConSQL()
        connection = c.conclass()
    }
}