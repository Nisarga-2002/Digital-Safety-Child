package com.example.digitalsafety_child.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.constants.Constants
import com.example.digitalsafety_child.model.MapDataModel
import com.example.digitalsafety_child.utils.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import java.util.Locale


class LocationService : Service() {
    private var builder: LocationParams.Builder? = null
    private var mNotificationManager: NotificationManager? = null

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    var latitude: Double = 0.0
    var longitude: Double = 0.0


    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        builder = LocationParams.Builder()
            .setAccuracy(LocationAccuracy.HIGH)
            .setInterval(Constants.LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS.toLong())

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        mNotificationManager!!.createNotificationChannel(mChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, notification)
        SmartLocation.with(this)
            .location()
            .continuous()
            .config(builder!!.build())
            .start { location ->
                Toast.makeText(
                    this,
                    location.latitude.toString() + " " + location.longitude.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                latitude = location.latitude
                longitude = location.longitude
                displayMap(latitude, longitude)
                storeDetails(latitude, longitude)
            }
        return START_STICKY
    }


    private val notification: Notification
        get() {
            val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentText(getString(R.string.body_text))
                .setContentTitle(getString(R.string.header_text))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification)

            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID)

            return builder.build()
        }

    private fun displayMap(latitude: Double, longitude: Double) {
        val addresses: List<Address>?
        val geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )


        val address =
            addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//        val city = addresses[0].locality
//        val state = addresses[0].adminArea
//        val country = addresses[0].countryName
//        val postalCode = addresses[0].postalCode
//        val knownName = addresses[0].featureName
//        val extras =   addresses[0].extras
//         val location =  addresses[0].locale
//         val subLocality = addresses[0].subLocality
//        val url =addresses[0].url

        Toast.makeText(this, " $address ", Toast.LENGTH_LONG).show()

//        Log.d("extras---->",extras.toString())
//        Log.d("location----->",location.toString())
//        Log.d("subLocality---->",subLocality.toString())
//        Log.d("URL----->",url.toString())
//        Log.d("Address---->>",address)
    }

    private fun storeDetails(latitude: Double, longitude: Double) {

        val m1 = MapDataModel(latitude, longitude)

        firebaseDatabase = FirebaseDatabase.getInstance()

        databaseReference = firebaseDatabase!!.getReference("MapDataModel")
        databaseReference!!.child(Constants.Location.Location_Path).child(SharedPreferences.getString(Constants.SharedPreferences.emailId,"Location ID").toString())
            .setValue(m1)

//        if (latitude == 0.0 && longitude == 0.0) {
//            databaseReference!!.child(Constants.Location_Path).child(Constants.Location_ID)
//                .setValue(m1)
//        }
//        else {
//            databaseReference!!.child(Constants.Location_Path).child(Constants.Location_ID)
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (datasnap in snapshot.children) {
//                            datasnap.ref.setValue(m1).addOnSuccessListener {
//                                Toast.makeText(
//                                    applicationContext, "Data successfully update",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                    override fun onCancelled(error: DatabaseError) {
//                        throw error.toException()
//                    }
//                })
//        }
    }
}