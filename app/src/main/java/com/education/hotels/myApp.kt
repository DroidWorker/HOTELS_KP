package com.education.hotels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Connection


class MyApp : Application() {
    val dataLoadedLiveData = MutableLiveData<Boolean>()

    var connection: Connection? = null
    lateinit var database: FirebaseDatabase
    private var subCount = 0
    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance("https://hotels-fd309-default-rtdb.europe-west1.firebasedatabase.app/")
        super.onCreate()
        connect()
    }

    private fun connect() {
        val myRef = database.getReference("ip")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ip = dataSnapshot.value
                val c = ConSQL(ip as String?)
                connection = c.conclass()
                dataLoadedLiveData.postValue(true)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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