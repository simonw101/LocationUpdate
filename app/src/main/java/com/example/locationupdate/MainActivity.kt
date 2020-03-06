package com.example.locationupdate

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import java.io.IOException
import java.util.*

private const val TAG = "MainActivity"

private val client = OkHttpClient()

class MainActivity : AppCompatActivity() {

    var currentWeatherModel = CurrentWeatherModel()

    private var locationManager: LocationManager? = null

    private val LOCATION_REQUEST_CODE = 101

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Unable to get users location ")

                } else {

                    try {

                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            10000L,
                            10f,
                            locationListener
                        )

                    } catch (e: SecurityException) {

                        e.printStackTrace()

                    }

                }

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "permission Denied")

        } else {

            try {

                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10000L,
                    10f,
                    locationListener
                )

            } catch (e: SecurityException) {

                e.printStackTrace()

            }

        }

        restoreState()

        Log.i(TAG, "onCreate")


    }

    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location?) {

            val url =
                "https://api.darksky.net/forecast/ea433c8422ff91fb7995532f7887621a/${location?.latitude},${location?.longitude}"

            Log.i(TAG, url)

            getLocation(location)

            try {
                run(url)
            } catch (e: Exception) {

                e.printStackTrace()

            }

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }
    }

    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    val result = response.body?.string()

                    if (response.isSuccessful) {

                        runOnUiThread {

                            try {

                                val parseJson = ParseJson()

                                if (result != null) {

                                    currentWeatherModel = parseJson.parse(result)

                                    summaryTV.text = currentWeatherModel.summary

                                    timeTV.text = currentWeatherModel.time

                                    temperatureTV.text = currentWeatherModel.temp

                                    chanceOfRainTV.text = currentWeatherModel.chanceOFRain

                                    humidityTV.text = currentWeatherModel.humidity


                                    //icons

                                    when (currentWeatherModel.icon) {
                                        "clear-day" -> {

                                            weatherIcon.setImageResource(R.drawable.sun)

                                        }
                                        "clear-night" -> {

                                            weatherIcon.setImageResource(R.drawable.clearnight)

                                        }
                                        "rain" -> {

                                            weatherIcon.setImageResource(R.drawable.rain)

                                        }
                                        "snow" -> {

                                            weatherIcon.setImageResource(R.drawable.snowing)

                                        }
                                        "sleet" -> {

                                            weatherIcon.setImageResource(R.drawable.sleet)

                                        }
                                        "wind" -> {

                                            weatherIcon.setImageResource(R.drawable.wind)

                                        }
                                        "fog" -> {

                                            weatherIcon.setImageResource(R.drawable.fog)

                                        }
                                        "cloudy" -> {

                                            weatherIcon.setImageResource(R.drawable.cloudy)

                                        }
                                        "partly-cloudy-day" -> {

                                            weatherIcon.setImageResource(R.drawable.partlycloudyday)

                                        }
                                        "partly-cloudy-night" -> {

                                            weatherIcon.setImageResource(R.drawable.partlycloudnight)
                                        }
                                    }
                                }

                            } catch (e: JSONException) {

                                e.printStackTrace()

                            }

                            savedState()

                        }
                    }

                }
            }
        })
    }

    fun getLocation(location: Location?) {

        val geoCoder = Geocoder(applicationContext, Locale.ENGLISH)

        try {

            if (location?.latitude != null) {
                val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                if (address.size > 0) {

                    val fetchedAddress = address[0]

                    val strAddress = StringBuilder()

                    addressTV.text = "${strAddress.append(fetchedAddress.postalCode)}"

                }

            }

        } catch (e: IOException) {

            e.printStackTrace()

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun restoreState() {

        var pref: SharedPreferences = applicationContext.getSharedPreferences("myPref", 0)

        var savedData = pref.getString("postcode", null)

        addressTV.text = savedData

        var tempData = pref.getString("temp", null)

        temperatureTV.text = tempData

        var summaryData = pref.getString("summary", null)

        summaryTV.text = summaryData

        var humidityData = pref.getString("humidity", null)

        humidityTV.text = humidityData

        var rainData = pref.getString("rain", null)

        chanceOfRainTV.text = rainData

        var timeData = pref.getString("time", null)

        timeTV.text = timeData
    }

    fun savedState() {

        var pref: SharedPreferences = applicationContext.getSharedPreferences("myPref", 0)

        val editor = pref.edit()

        editor.putString("postcode", addressTV.text.toString())

        editor.putString("temp", temperatureTV.text.toString())

        editor.putString("summary", summaryTV.text.toString())

        editor.putString("humidity", humidityTV.text.toString())

        editor.putString("rain", chanceOfRainTV.text.toString())

        editor.putString("time", timeTV.text.toString())

        //editor.putInt("icon", weatherIcon.setImageResource())

        editor.apply()


    }

    override fun onStart() {
        super.onStart()

        restoreState()

    }

    override fun onPause() {
        super.onPause()

        restoreState()
    }

    override fun onResume() {
        super.onResume()

        restoreState()
    }

    override fun onRestart() {
        super.onRestart()

        restoreState()
    }

    override fun onStop() {
        super.onStop()

        savedState()
    }

    override fun onDestroy() {
        super.onDestroy()

        savedState()
    }

    fun refresh(view: View) {

        refeshButton.animate().rotationBy(360f).setDuration(3000).start()

    }

}
