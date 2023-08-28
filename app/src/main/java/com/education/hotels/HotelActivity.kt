package com.education.hotels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.VM.AppViewModel
import com.education.hotels.VM.Room
import com.education.hotels.adapters.RoomAdapterItem
import com.education.hotels.adapters.RoomAdapterTitle
import com.education.hotels.adapters.RoomsAdapter
import kotlinx.coroutines.launch

class HotelActivity : AppCompatActivity() {
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }
    lateinit var starField : TextView
    lateinit var hotelName : TextView
    lateinit var adapter: RoomsAdapter
    private var roomAdapterTitle: RoomAdapterTitle? = null
    var roomItemList:List<Room> = emptyList()
    var hotelname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)
        appViewModel.connection = (application as MyApp).connection
        (application as MyApp).subscribe()
        intent.getIntExtra("hotelId", 0).let { if(it!=0) {
            appViewModel.getHotelInfo(it)
            appViewModel.fetchRooms(it)
        }}
        starField = findViewById(R.id.starField)
        hotelName = findViewById(R.id.hotelName)

        adapter = RoomsAdapter(this)
        adapter.onItemClick={
            val intent = Intent(this@HotelActivity, RoomActivity::class.java)
            intent.putExtra("roomId", it)
            intent.putExtra("hotelName", hotelname)
            startActivity(intent)
        }
        val rv = findViewById<RecyclerView>(R.id.roomsRV)
        rv.layoutManager = LinearLayoutManager(this@HotelActivity)
        rv.adapter = adapter

        observe()
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApp).cancelSub()
    }

    private fun observe(){
        lifecycleScope.launch {
            appViewModel.hotelInfo.collect{hotel->
                if(hotel!=null){
                    roomAdapterTitle = RoomAdapterTitle(hotel.hotelAddress, hotel.hotelPhone, hotel.hotelEmail, hotel.roomInventory, hotel.hotelDirection)
                    val resultList : ArrayList<RoomAdapterItem> = ArrayList()
                    resultList.add(RoomAdapterItem(0, 1, roomAdapterTitle, null))
                    for(i in roomItemList.indices){
                        resultList.add(RoomAdapterItem(i+1, 0, null, roomItemList[i]))
                    }
                    adapter.setData(resultList)
                }
                var starText = ""
                try {
                    if (hotel != null) {
                        for (i in 1..hotel.classification.toInt()) {
                            starText+="⭐ "
                        }
                    }
                }catch (ex: Exception){
                    println("no number in string 75774")
                }
                starField.text = starText
                hotelName.text = hotel?.hotelName?:""
                hotelname = hotel?.hotelName?:""
            }

        }
        lifecycleScope.launch {
            appViewModel.rooms.collect{rooms->
                roomItemList = rooms
                val resultList : ArrayList<RoomAdapterItem> = ArrayList()
                resultList.add(RoomAdapterItem(0, 1, roomAdapterTitle, null))
                for(i in roomItemList.indices){
                    resultList.add(RoomAdapterItem(i+1, 0, null, roomItemList[i]))
                }
                adapter.setData(resultList)
            }

        }
    }
}