package com.education.hotels

import android.app.Application
import java.sql.Connection

class MyApp : Application() {
    var connection: Connection? = null
    private var subCount = 0
    override fun onCreate() {
        super.onCreate()
        connect()
    }

    private fun connect() {
        val c = ConSQL()
        connection = c.conclass()
    }

    fun subscribe(){
        subCount++
    }
    fun cancelSub(){
        subCount--
        if(subCount==0) {
            connection?.close()
            println("eeeeeerrrrrr connection closed")
        }
    }
}