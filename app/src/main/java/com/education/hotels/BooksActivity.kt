package com.education.hotels

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.education.hotels.VM.AppViewModel
import com.education.hotels.adapters.BooksAdapter
import kotlinx.coroutines.launch


class BooksActivity : AppCompatActivity() {
    private val appViewModel by lazy { ViewModelProvider(this)[AppViewModel::class.java] }

    lateinit var adapter: BooksAdapter

    var userPhone = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        appViewModel.connection = (application as MyApp).connection
        (application as MyApp).subscribe()

        appViewModel.userPhone.let {
            if (it != null) {
                userPhone = it
                collect()
            }else{
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Введите номер телефона для поиска брони")

                val input = EditText(this)
                input.inputType =
                    InputType.TYPE_CLASS_PHONE
                builder.setView(input)

                builder.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        /*if(input.text.matches(Regex("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$"))) {
                            userPhone= input.text.toString()
                            appViewModel.userPhone = userPhone
                        }else{
                            input.error = "Ошибка"
                        }*/
                    })
                builder.setNegativeButton("Отмена",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                        this@BooksActivity.finish()
                    })
                builder.setCancelable(false)
                builder.show().let { dial ->
                    dial.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (input.text.matches(Regex("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$"))) {
                            userPhone = input.text.toString()
                            appViewModel.userPhone = userPhone
                            dial.dismiss()
                            collect()
                        } else {
                            input.error = "Ошибка"
                        }
                    }
                }
            }
        }

        adapter = BooksAdapter(this)
        adapter.onItemClick={
            val intent = Intent(this@BooksActivity, HotelActivity::class.java)
            intent.putExtra("hotelId", it)
            startActivity(intent)
        }
        val rv = findViewById<RecyclerView>(R.id.bookingRV)
        rv.layoutManager = LinearLayoutManager(this@BooksActivity)
        rv.adapter = adapter

        observe()
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as MyApp).cancelSub()
    }

    private fun collect(){
        appViewModel.fetchBookingInfoByPhone(userPhone)
    }

    private fun observe(){
        lifecycleScope.launch {
            appViewModel.bookingIfo.collect{booking->
                adapter.setData(booking)
            }

        }
    }
}