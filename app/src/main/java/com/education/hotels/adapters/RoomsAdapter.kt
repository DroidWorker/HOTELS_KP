package com.education.hotels.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.R
import com.education.hotels.VM.Room

class RoomsAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var rooms: List<RoomAdapterItem> = emptyList()
    var ctx: Context? = null

    var onItemClick: ((Int) -> Unit)? = null

    init {
        ctx = context
    }

    fun setData(roomsList: List<RoomAdapterItem>){
        rooms = roomsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==0) RoomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hotel_item, parent, false))
        else AdapterTitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.hotel_activity_info_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(getItem(position)) {
            if(this!=null) {
                if (adapterItem != null) (holder as RoomViewHolder).bind(adapterItem!!)
                else if(adapterTitle != null) (holder as AdapterTitleViewHolder).bind(adapterTitle!!)
            }
        }
    }

    fun getItem(id: Int): RoomAdapterItem? = rooms.find { it.uniqueId == id }
    override fun getItemViewType(position: Int) = getItem(position)?.viewType?:0
    override fun getItemCount() = rooms.size

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(room: Room) {
            itemView.setOnClickListener{
                onItemClick?.invoke(room.roomId)
                itemView.findViewById<TextView>(R.id.room_number).text = room.roomNumber
                itemView.findViewById<TextView>(R.id.is_free_now).text = if(room.availability) "Доступен сейчас" else "Недоступен сейчас"
                itemView.findViewById<TextView>(R.id.places_num).text = room.placesNumber.toString()+(if(room.placesNumber==1)" место" else if(room.placesNumber in 2..4)" места" else " мест")
                itemView.findViewById<TextView>(R.id.room_price).text = room.roomPrice.toString()+" р."

            }

        }
    }

    inner class AdapterTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(adapterTitle: RoomAdapterTitle) {
            (itemView.findViewById<TextView>(R.id.hotel_address)).text = adapterTitle.hotelAddress
            (itemView.findViewById<TextView>(R.id.hotel_email)).text = adapterTitle.hotelEmail
            (itemView.findViewById<TextView>(R.id.hotel__phone)).text = adapterTitle.hotelPhone
            (itemView.findViewById<TextView>(R.id.textView4)).text = "Доступно номеров: "+adapterTitle.roomAvailable
            (itemView.findViewById<ImageView>(R.id.imageView))
        }
    }
}

data class RoomAdapterItem(
    var uniqueId: Int,
    var viewType: Int,
    var adapterTitle: RoomAdapterTitle?,
    var adapterItem: Room?
)

data class RoomAdapterTitle(
    var hotelAddress: String,
    var hotelPhone: String,
    var hotelEmail: String,
    var roomAvailable: Int,
    var photo: String
)
