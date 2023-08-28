package com.education.hotels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.VM.AppViewModel
import com.education.hotels.adapters.HotelsAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }
    lateinit var adapter: HotelsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApp).dataLoadedLiveData.observe(this) { dataLoaded ->
            if (dataLoaded) {
                appViewModel.connection = (application as MyApp).connection
                (application as MyApp).subscribe()
                adapter = HotelsAdapter(this)
                adapter.onItemClick = { hotelId ->
                    val intent = Intent(this, HotelActivity::class.java)
                    intent.putExtra("hotelId", hotelId)
                    startActivity(intent)
                }
                val rv = findViewById<RecyclerView>(R.id.hotelsRV)
                rv.adapter = adapter
                rv.layoutManager = LinearLayoutManager(this@MainActivity)
                appViewModel.fetchHotels()
                observe()
                findViewById<ImageButton>(R.id.myBookingButton).setOnClickListener {
                    val intent = Intent(this@MainActivity, BooksActivity::class.java)
                    startActivity(intent)
                }
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApp).cancelSub()
    }

    private fun observe(){
        lifecycleScope.launch {
            appViewModel.hotels.collect{
                adapter.setData(it)
            }
        }
    }
}