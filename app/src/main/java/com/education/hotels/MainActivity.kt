package com.education.hotels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.education.hotels.VM.AppViewModel
import com.education.hotels.adapters.HotelsAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }
    lateinit var adapter: HotelsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = HotelsAdapter(this)
        val rv = findViewById<RecyclerView>(R.id.hotelsRV)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this@MainActivity)
        appViewModel.fetchHotels()
        observe()
    }

    private fun observe(){
        lifecycleScope.launch {
            appViewModel.hotels.collect{
                println("eeeeeerrrrrr"+it)
                adapter.setData(it)
            }
        }
    }
}