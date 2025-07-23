package com.example.digitalsafety_child.activity

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.constants.Constants
import com.example.digitalsafety_child.databinding.ActivityPermissionOverviewBinding
import com.example.digitalsafety_child.model.PermissionOverview
import com.example.digitalsafety_child.permissionUtils.AdminReceiver
import com.example.digitalsafety_child.permissionUtils.PermissionHelper
import com.example.digitalsafety_child.permissionUtils.PermissionOverviewAdapter
import com.example.digitalsafety_child.services.CameraxService
import com.example.digitalsafety_child.utils.setWindowInsets

class PermissionOverviewActivity : AppCompatActivity() {
    private lateinit var permissionOverviewAdapter: PermissionOverviewAdapter
    private lateinit var binding: ActivityPermissionOverviewBinding
    private val permissionHelper = PermissionHelper(this)
    var dpm: DevicePolicyManager? = null
    var compName: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.permissionOverviewLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        permissionOverviewAdapter = PermissionOverviewAdapter()
        compName = ComponentName(this, AdminReceiver::class.java)
        dpm = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val listOfPermission = mutableListOf<PermissionOverview>()
        listOfPermission.add(
            PermissionOverview(
                Constants.PermissionId.DEVICE_ADMINISTRATION,
                R.drawable.ic_device_administration,
               getString(R.string.device_administrator),
                getString(R.string.administrator_description),
            )
        )
        listOfPermission.add(
            PermissionOverview(
                Constants.PermissionId.LOCATION,
                R.drawable.ic_location,
                getString(R.string.location),
                getString(R.string.location_description),
            )
        )
        listOfPermission.add(
            PermissionOverview(
                Constants.PermissionId.OVERLAY,
                R.drawable.ic_overlay,
                getString(R.string.overlay),
                getString(R.string.overlay_description),
            )
        )
        if(permissionHelper.deviceHasNewMIUIVersion()){
            listOfPermission.add(
                PermissionOverview(
                    Constants.PermissionId.DISPLAY_POP_UP_IN_BACKGROUND,
                    R.drawable.ic_display_pop_bg,
                    getString(R.string.display_pop_up_in_background),
                    getString(R.string.pop_up_background_description),
                )
            )
            }
        listOfPermission.add(
        PermissionOverview(
            Constants.PermissionId.CAMERA,
                R.drawable.ic_camera,
                getString(R.string.camera),
                getString(R.string.camera_description),
            )
        )

        binding.recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        permissionOverviewAdapter.setListOfPermissionOverview(listOfPermission)
        binding.recyclerView.adapter = permissionOverviewAdapter

        permissionOverviewAdapter.onClickListener = {
            when(it.id){
                Constants.PermissionId.LOCATION -> {
                    startActivity(
                        Intent(
                            this@PermissionOverviewActivity,
                            LocationScreenActivity::class.java
                        )
                    )
                }
                Constants.PermissionId.OVERLAY ->{
                    permissionHelper.checkOverlayPermission()
                }
                Constants.PermissionId.DEVICE_ADMINISTRATION ->{
                    becomeDeviceAdmin()
                }
                Constants.PermissionId.DISPLAY_POP_UP_IN_BACKGROUND ->{
                    permissionHelper.requestDisplayPopUPBackground()
                }
                Constants.PermissionId.CAMERA -> {
                    permissionHelper.requestCamera()
                }
            }
        }
        startService()

        setWindowInsets(binding.permissionOverviewLayout)
    }

    private fun startService() {
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(
                this,
                CameraxService::class.java
            )
            startForegroundService(intent)
        }
    }
    private fun isActiveAdmin(): Boolean? {
        return compName?.let { dpm?.isAdminActive(it) ?: false }
    }

    private fun becomeDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "ACTIVATE THE APP")
        startActivityForResult(intent, Activity.RESULT_OK)
    }



}