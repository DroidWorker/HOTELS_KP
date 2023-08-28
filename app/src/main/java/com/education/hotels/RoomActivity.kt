package com.education.hotels

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.education.hotels.VM.AppViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


class RoomActivity : AppCompatActivity() {
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }

    var roomId: Int = 0
    lateinit var roomNum: TextView
    lateinit var placesCount: TextView
    lateinit var chekinDate: DatePicker
    lateinit var chekoutDate: DatePicker
    lateinit var name: EditText
    lateinit var pphone: EditText
    lateinit var summ: TextView
    lateinit var buttonOk: Button

    private var checkindate: Calendar = Calendar.getInstance()
    private var checkoutdate: Calendar = Calendar.getInstance()
    private var price: BigDecimal = BigDecimal(0)
    var totalPrice = BigDecimal(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        appViewModel.connection = (application as MyApp).connection
        (application as MyApp).subscribe()

        roomNum = findViewById(R.id.room_num)
        placesCount = findViewById(R.id.places_count)
        chekinDate = findViewById(R.id.checkin_datepicker)
        chekoutDate = findViewById(R.id.checkout_datepicker)
        name = findViewById(R.id.editTextName)
        pphone = findViewById(R.id.editTextPhone)
        summ = findViewById(R.id.summTV)
        buttonOk = findViewById(R.id.button)

        appViewModel.userPhone.let {
            if (it != null)
                pphone.setText(it)
        }

        intent.getIntExtra("roomId", 0).let{
            appViewModel.fetchRoomInfoById(it)
            roomId = it
        }
        intent.getStringExtra("hotelName")?.let{(findViewById<TextView>(R.id.hotel_name2)).text = it}

        chekinDate.init(checkindate.get(Calendar.YEAR), checkindate.get(Calendar.MONTH),
            checkindate.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            checkindate.set(year, month, day)
            if (checkindate > checkoutdate) {
                checkoutdate = checkindate.clone() as Calendar
                chekoutDate.updateDate(year, month, day)
            }
            calculateSumm()
        }
        chekoutDate.init(checkoutdate.get(Calendar.YEAR), checkoutdate.get(Calendar.MONTH),
            checkoutdate.get(Calendar.DAY_OF_MONTH),
            OnDateChangedListener { datePicker, year, month, day ->
                checkoutdate.set(year, month, day)
                if(checkindate > checkoutdate) {
                    checkoutdate = checkindate
                    checkindate.let{chekoutDate.updateDate(it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH))}
                    Toast.makeText(this@RoomActivity, "Дата выезда не может быть раньше даты заселения", Toast.LENGTH_SHORT).show()
                }
                calculateSumm()
            }
        )

        buttonOk.setOnClickListener{
            if(validate()){
                appViewModel.userPhone = pphone.text.toString()
                appViewModel.bookRoom(
                    roomId,
                    name.text.toString(),
                    pphone.text.toString(),
                    Timestamp(checkindate.time.time),
                    Timestamp(checkoutdate.time.time),
                    totalPrice
                    )
                finish()
            }
        }

        observe()
    }

    override fun onResume() {
        super.onResume()
        appViewModel.fetchRoomInfoById(roomId)
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApp).cancelSub()
    }

    private fun observe() {
        lifecycleScope.launch {
            appViewModel.roomInfo.collect { room ->
                roomNum.text = "Комната ${room?.roomNumber}"
                placesCount.text = room?.placesNumber.toString()+(if(room?.placesNumber==1)" место" else if(room?.placesNumber in 2..4)" места" else " мест")
                price = room?.roomPrice?: BigDecimal(0)
            }
        }
    }

    private fun validate(): Boolean{
        if(!name.text.matches(Regex("^[a-zA-Zа-яА-Я ]+$"))) {
            name.error = "Ошибка"
            return false
        }else if(!pphone.text.matches(Regex("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$"))){
            pphone.error = "Ошибка"
            return false
        }else return true
    }

    private fun calculateSumm(){
        val result : Long = checkoutdate.time.time - checkindate.time.time
        val days = TimeUnit.MILLISECONDS.toDays(result)
        totalPrice = (price.multiply(days.toBigDecimal()))
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        summ.text = (df.format(totalPrice)).toString()+" p."
    }
}