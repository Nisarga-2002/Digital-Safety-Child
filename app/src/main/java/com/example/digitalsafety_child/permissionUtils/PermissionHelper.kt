package com.example.digitalsafety_child.permissionUtils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import java.lang.reflect.Method

class PermissionHelper(private val activity: Activity) {

     val OVERLAY = 104
     val ACTIVATE_DEVICE_ADMIN = 102
    val REQUIRED_PERMISSIONS: Array<String> = arrayOf(Manifest.permission.CAMERA)
    val REQUEST_CODE_PERMISSIONS: Int = 20

    fun deviceHasNewMIUIVersion(): Boolean {
        var result = false
        val manufacturer = "xiaomi"
        if (manufacturer.equals(Build.MANUFACTURER, true)) {
            try {
                @SuppressLint("PrivateApi") val propertyClass =
                    Class.forName("android.os.SystemProperties")
                val method: Method = propertyClass.getMethod("get", String::class.java)
                val versionName: String =
                    method.invoke(propertyClass, "ro.miui.ui.version.name")?.toString() ?: ""

                val versionStringIndex = Regex("[vV]").find(versionName, 0)?.range?.start ?: -1
                if (versionStringIndex > -1 && versionName.length > versionStringIndex) {
                    val versionValue =
                        versionName.substring(versionStringIndex + 1, versionName.length)
                    if (versionValue.isDigitsOnly() && versionValue.toInt() >= 10)
                        result = true
                }
            } catch (e: Exception) {
                Log.e(
                    PermissionHelper::class.java.simpleName,
                    "An error occurred while Extracting xiaomi version -  ${e.message}"
                )
            }
        }
        return result
    }

    fun requestDisplayPopUPBackground() {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", activity.packageName)
        activity.startActivity(intent)
    }
    fun requestCamera(){
        ActivityCompat.requestPermissions(
            activity,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }
    fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(activity)) {
            val myIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                    "package:${activity.packageName}"
                )
            )
            activity.startActivity(myIntent)
        }
    }

}