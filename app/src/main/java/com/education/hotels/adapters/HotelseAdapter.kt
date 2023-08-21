package com.education.hotels.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.R
import com.education.hotels.VM.Hotel

class HotelsAdapter(context: Context) : RecyclerView.Adapter<HotelsAdapter.HotelViewHolder>() {
    var hotels: List<Hotel> = emptyList()
    var ctx: Context? = null
    init {
        ctx = context
    }

    fun setData(hotelsList: List<Hotel>){
        hotels = hotelsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hotel_item, parent, false)
        return HotelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val currentHotel = hotels[position]
        holder.bind(currentHotel)
    }

    override fun getItemCount() = hotels.size

    inner class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hotel: Hotel) {
            val frc =  itemView.findViewById<TextView>(R.id.free_rooms_count)
            var starText = ""
            try {
                for (i in 1..hotel.classification.toInt()) {
                    starText+="⭐ "
                }
            }catch (ex: Exception){
                println("no number in string 75774")
            }
            itemView.findViewById<TextView>(R.id.hotel_name).text = hotel.hotelName
            itemView.findViewById<TextView>(R.id.addres).text = hotel.hotelAddress
            itemView.findViewById<TextView>(R.id.hotel_classifcation).text = starText
           frc.text = "${hotel.freeRoom}/${hotel.roomInventory}"
            when(hotel.roomInventory/hotel.freeRoom){
                in 0..3 -> {
                    frc.background = ctx!!.resources.getDrawable(R.drawable.bgreen)
                }
                else -> {
                    frc.background = ctx!!.resources.getDrawable(R.drawable.bgred)
                }
            }
        }
    }
}
