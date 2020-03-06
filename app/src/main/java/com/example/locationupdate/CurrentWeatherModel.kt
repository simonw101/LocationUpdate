package com.example.locationupdate

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CurrentWeatherModel() {

    var summary: String = ""

    var time: String = ""

    var temp: String = ""

    var icon: String = ""

    var chanceOFRain: String = ""

    var humidity: String = ""

    fun convertTempToCelcius(tempVar: String) : String {

        val result = (tempVar.toDouble() - 32) * 0.5555555556

        return String.format("%.0f", result) + "\u00B0" + "C"
    }

    fun chanceOfRainFun(rainVar: String) : String {

        val result = (rainVar.toDouble() * 100)

        return "Precipitation: " + String.format("%.0f", result) + "%"
    }

    fun humidityFun(humidityVar: String) : String {

        val result = (humidityVar.toDouble() * 100)

        return "Humidity: " + String.format("%.0f", result) + "%"

    }

    fun formattedTime() : String {

        val timeStamp = Timestamp(System.currentTimeMillis())

        val date = Date(timeStamp.time)

        val format = SimpleDateFormat("dd/MM HH:mm:ss")

        return "Last updated: ${format.format(date)}"

    }
}