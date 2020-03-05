package com.example.locationupdate

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

private const val TAG = "ParseJson"

class ParseJson {

    var currentWeatherModel = CurrentWeatherModel()

    fun parse(jsonData: String): CurrentWeatherModel {

        try {

            val data = JSONObject(jsonData)

            var currently = data.getJSONObject("currently")

            var summary = currently.getString("summary")

            var time = currently.getString("time")

            var temp = currently.getString("temperature")

            var icon = currently.getString("icon")

            var chanceOFRain = currently.getString("precipProbability");

            var humidity = currently.getString("humidity")

            currentWeatherModel.summary = summary

            currentWeatherModel.time = time

            currentWeatherModel.temp = currentWeatherModel.convertTempToCelcius(temp)

            currentWeatherModel.icon = icon

            currentWeatherModel.chanceOFRain = chanceOFRain

            currentWeatherModel.humidity = humidity

            currentWeatherModel.icon  = icon

        } catch (e: JSONException) {

            e.printStackTrace()

        }

        return currentWeatherModel
    }

}