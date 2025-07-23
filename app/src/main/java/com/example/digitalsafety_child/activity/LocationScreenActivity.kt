package com.example.digitalsafety_child.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.databinding.ActivityLocationScreenBinding
import com.example.digitalsafety_child.services.LocationService

class LocationScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationScreenBinding
    private val LOCATION_PERMISSIONS_REQUEST = 101
    val BACKGROUND_LOCATION_PERMISSION_REQUEST = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val message: String = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getString(R.string.location_permission_alert_dialog_description_one)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            getString(R.string.location_permission_alert_dialog_description_three)
        } else {
            getString(R.string.location_permission_alert_dialog_description_two)
        }

        binding.permissionDetailsDescription.text = message

        binding.Allow.setOnClickListener {
            if (!isLocationPermissionGranted()) {
                requestPermissions()
//            Toast.makeText(this,"MainActivity--->",Toast.LENGTH_LONG).show()
            } else {
                registerService()
            }
        }


    }

    private fun registerService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startService(serviceIntent)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSIONS_REQUEST
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestBackgroundLocationPermission()
            }
        }
    }

    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            BACKGROUND_LOCATION_PERMISSION_REQUEST
        )
    }

}