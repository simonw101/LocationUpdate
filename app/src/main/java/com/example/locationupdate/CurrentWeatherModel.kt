package com.example.locationupdate

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
}