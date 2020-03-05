package com.example.locationupdate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
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
                            0L,
                            0f,
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
                    0L,
                    0f,
                    locationListener
                )

            } catch (e: SecurityException) {

                e.printStackTrace()

            }

        }
    }

    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location?) {

            var url =
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

                                    Log.i(TAG, currentWeatherModel.icon)

                                    chanceOfRainTV.text = currentWeatherModel.chanceOFRain

                                    humidityTV.text = currentWeatherModel.humidity

                                    //icons

                                    if (currentWeatherModel.icon == "clear-day") {

                                        weatherIcon.setImageResource(R.drawable.sun)

                                    } else if (currentWeatherModel.icon == "clear-night") {

                                        weatherIcon.setImageResource(R.drawable.clearnight)

                                    } else if (currentWeatherModel.icon == "rain") {

                                        weatherIcon.setImageResource(R.drawable.rain)

                                    } else if (currentWeatherModel.icon == "snow") {

                                        weatherIcon.setImageResource(R.drawable.snowing)

                                    } else if (currentWeatherModel.icon == "sleet") {

                                        weatherIcon.setImageResource(R.drawable.sleet)

                                    } else if (currentWeatherModel.icon == "wind") {

                                        weatherIcon.setImageResource(R.drawable.wind)

                                    } else if (currentWeatherModel.icon == "fog") {

                                        weatherIcon.setImageResource(R.drawable.fog)

                                    } else if (currentWeatherModel.icon == "cloudy") {

                                        weatherIcon.setImageResource(R.drawable.cloudy)

                                    } else if (currentWeatherModel.icon == "partly-cloudy-day") {

                                        weatherIcon.setImageResource(R.drawable.partlycloudyday)

                                    } else if (currentWeatherModel.icon == "partly-cloudy-night") {

                                        weatherIcon.setImageResource(R.drawable.partlycloudnight)
                                    }

                                    Log.i(TAG, currentWeatherModel.icon)
                                }

                            } catch (e: JSONException) {

                                e.printStackTrace()

                            }

                        }
                    }


                }
            }
        })
    }

    fun getLocation(location: Location?) {

        val geoCoder = Geocoder(applicationContext, Locale.ENGLISH)

        try {

            if (location?.latitude != null && location?.longitude != null) {
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
}
