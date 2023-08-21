package com.education.hotels.VM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.hotels.ConSQL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.sql.*

class AppViewModel : ViewModel() {

     val hotels = MutableStateFlow<List<Hotel>>(emptyList())
     val hotelInfo = MutableStateFlow<HotelInfo?>(null)
     val rooms = MutableStateFlow<List<Room>>(emptyList())
     val bookingIfo = MutableStateFlow<List<BookingInfo>>(emptyList())

    // Инициализация базы данных и соединения
    private val url = "jdbc:sqlserver://192.168.1.9:1433/DESKTOP-F1CTFP8"
    private val user = "mobileConnection"
    private val password = "mobileConnection"
    private var connection: Connection? = null

    init {
        connect()
    }

    private fun connect() {
        val c = ConSQL()
        connection = c.conclass()
    }

    private fun disconnect() {
        connection?.close()
    }

    // Получение данных о гостиницах из базы данных
    fun fetchHotels() {
        val hotelsList = mutableListOf<Hotel>()
        val sql = "SELECT * FROM dbo.GetHotelsInfo();"
        try {
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery(sql)
            while (resultSet?.next() == true) {
                val hotelId = resultSet.getInt("hotel_id")
                val hotelName = resultSet.getString("hotel_name")
                val hotelAddress = resultSet.getString("hotel_address")
                val classification = resultSet.getString("hotel_classification")
                val roomInventory = resultSet.getInt("room_inventory")
                val freeRooms = resultSet.getInt("free_rooms")
                hotelsList.add(
                    Hotel(
                        hotelId,
                        hotelName,
                        hotelAddress,
                        classification,
                        roomInventory,
                        freeRooms
                    )
                )
            }
            hotels.tryEmit(hotelsList)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getHotelInfo(id: Int) {
        var _hotelInfo : HotelInfo? = null
        val sql = "SELECT * FROM dbo.GetHotelInfoById(@hotel_id);"
        try {
            val preparedStatement = connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, id)

            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                val hotelId = resultSet.getInt("hotel_id")
                val hotelName = resultSet.getString("hotel_name")
                val hotelAddress = resultSet.getString("hotel_address")
                val hotelPhone = resultSet.getString("phone ")
                val hotelEmail = resultSet.getString("email")
                val hotelDirection = resultSet.getString("directions")
                val classification = resultSet.getString("hotel_classification")
                val roomInventory = resultSet.getInt("room_inventory")

                _hotelInfo = HotelInfo(
                        hotelId,
                        hotelName,
                        hotelAddress,
                        hotelPhone,
                        hotelEmail,
                        hotelDirection,
                        classification,
                        roomInventory
                    )
            }
            hotelInfo.tryEmit(_hotelInfo)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    // Получение данных о номерах из базы данных
    fun fetchRooms(id: Int) {
        val roomsList = mutableListOf<Room>()
        val sql = "SELECT * FROM dbo.GetRoomsInfoByHotelId(@hotel_id)"
        try {
            val preparedStatement = connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, id)

            val resultSet = preparedStatement?.executeQuery()
            while (resultSet?.next() == true) {
                val roomId = resultSet.getInt("room_id")
                val roomNumber = resultSet.getString("room_number")
                val freeNow = resultSet.getBoolean("room_freenow")
                val placesNumber = resultSet.getInt("places_number")
                val roomPrice = resultSet.getBigDecimal("room_price")
                roomsList.add(Room(roomId, roomNumber, freeNow, placesNumber, roomPrice))
            }
            rooms.tryEmit(roomsList)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun searchAvailableRooms(checkInDate: Date, checkOutDate: Date) {
        val roomsList = mutableListOf<Room>()
        val sql = "{ CALL SearchAvailableRooms(?, ?) }"
        try {
            val callableStatement = connection?.prepareCall(sql)
            callableStatement?.setDate(1, java.sql.Date(checkInDate.time))
            callableStatement?.setDate(2, java.sql.Date(checkOutDate.time))

            val resultSet = callableStatement?.executeQuery()
            while (resultSet?.next() == true) {
                val roomId = resultSet.getInt("room_id")
                val roomNumber = resultSet.getString("room_number")
                val freeNow = resultSet.getBoolean("room_freenow")
                val placesNumber = resultSet.getInt("places_number")
                val roomPrice = resultSet.getBigDecimal("room_price")
                roomsList.add(Room(roomId, roomNumber, freeNow, placesNumber, roomPrice))
            }
            rooms.tryEmit(roomsList)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun fetchRoomInfoById(roomId: Int) {
        var _hotelInfo : HotelInfo? = null
        val sql = "SELECT * FROM dbo.GetRoomInfoById(@room_id);"
        try {
            val preparedStatement = connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, roomId)

            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                val hotelId = resultSet.getInt("hotel_id")
                val hotelName = resultSet.getString("hotel_name")
                val hotelAddress = resultSet.getString("hotel_address")
                val hotelPhone = resultSet.getString("phone ")
                val hotelEmail = resultSet.getString("email")
                val hotelDirection = resultSet.getString("directions")
                val classification = resultSet.getString("hotel_classification")
                val roomInventory = resultSet.getInt("room_inventory")

                _hotelInfo = HotelInfo(
                    hotelId,
                    hotelName,
                    hotelAddress,
                    hotelPhone,
                    hotelEmail,
                    hotelDirection,
                    classification,
                    roomInventory
                )
            }
            hotelInfo.tryEmit(_hotelInfo)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun bookRoom(roomId: Int, customerName: String, customerPhone: String, checkInDate: Timestamp, checkOutDate: Timestamp, totalPrice: BigDecimal) {
        try {
            // SQL-запрос для вызова хранимой процедуры AddBooking
            val sql = "{ CALL InsertBooking(?, ?, ?, ?, ?, ?) }"

            // Создание CallableStatement
            val callableStatement = connection?.prepareCall(sql)
            callableStatement?.setInt(1, roomId)
            callableStatement?.setTimestamp(2, Timestamp(System.currentTimeMillis()))
            callableStatement?.setString(3, customerName)
            callableStatement?.setString(4, customerPhone)
            callableStatement?.setTimestamp(5, checkInDate)
            callableStatement?.setTimestamp(6, checkOutDate)
            callableStatement?.setBigDecimal(7, totalPrice)

            // Выполнение запроса
            callableStatement?.execute()

            // Закрытие ресурсов
            callableStatement?.close()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun fetchBookingInfoByPhone(phoneNumber: String) {
        var bookingInfo = mutableListOf<BookingInfo>()
        val sql = "SELECT * FROM dbo.GetBookingInfoByPhoneNumber(@phone_number);"
        try {
            val preparedStatement = connection?.prepareStatement(sql)
            preparedStatement?.setString(1, phoneNumber)

            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                val name = resultSet.getString("customer_name")
                val checkInDate = resultSet.getString("checkin_date")
                val checkOutDate = resultSet.getString("checkout_date")
                val placesNumber = resultSet.getInt("places_number")
                val totalPrice = resultSet.getInt("total_price")
                val hotelId = resultSet.getInt("hotel_id")

                bookingInfo.add(BookingInfo(
                    name,
                    checkInDate,
                    phoneNumber,
                    checkOutDate,
                    placesNumber,
                    totalPrice,
                    hotelId
                ))
            }
            bookingIfo.tryEmit(bookingInfo)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    // Закрытие соединения с базой данных
    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}

data class Hotel(
    val hotelId: Int,
    val hotelName: String,
    val hotelAddress: String,
    val classification: String,
    val roomInventory: Int,
    val freeRoom: Int
)

data class HotelInfo(
    val hotelId: Int,
    val hotelName: String,
    val hotelAddress: String,
    val hotelPhone: String,
    val hotelEmail: String,
    val hotelDirection: String,
    val classification: String,
    val roomInventory: Int
)

data class Room(
    val roomId: Int,
    val roomNumber: String,
    val availability: Boolean,
    val placesNumber: Int,
    val roomPrice: BigDecimal
)

data class BookingInfo(
    val name: String,
    val checkInDate: String,
    val phoneNumber: String,
    val checkOutDate: String,
    val placesNumber: Int,
    val totalPrice: Int,
    val hotelId: Int
)