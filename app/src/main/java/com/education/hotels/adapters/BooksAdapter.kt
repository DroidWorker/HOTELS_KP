package com.education.hotels.adapters
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.R
import com.education.hotels.VM.BookingInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BooksAdapter(context: Context) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {
    var booking: List<BookingInfo> = emptyList()
    var ctx: Context? = null

    var onItemClick: ((Int) -> Unit)? = null

    init {
        ctx = context
    }

    fun setData(bookingList: List<BookingInfo>){
        booking = bookingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = booking[position]
        holder.bind(currentBook)
    }

    override fun getItemCount() = booking.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(booking: BookingInfo) {
            itemView.setOnClickListener{
                onItemClick?.invoke(booking.hotelId)
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val datein = LocalDate.parse(booking.checkInDate, formatter)
            val dateout = LocalDate.parse(booking.checkOutDate, formatter)

            itemView.findViewById<TextView>(R.id.name_booking).text = booking.name
            itemView.findViewById<TextView>(R.id.phone_booking).text = booking.phoneNumber
            itemView.findViewById<TextView>(R.id.checkin_date_booking).text = datein.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            itemView.findViewById<TextView>(R.id.checkout_date_booking).text = dateout.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            itemView.findViewById<TextView>(R.id.room_number_booking).text = "комната № "+booking.roomNumber
            itemView.findViewById<TextView>(R.id.places_number_booking).text = booking.placesNumber.toString()+(if(booking.placesNumber==1)" место" else if(booking.placesNumber in 2..4)" места" else " мест")
            itemView.findViewById<TextView>(R.id.total_price_booking).text = booking.totalPrice.toString() + " p."
        }
    }
}
