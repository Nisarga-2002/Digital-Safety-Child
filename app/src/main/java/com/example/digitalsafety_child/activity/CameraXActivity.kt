package com.example.digitalsafety_child.activity

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.digitalsafety_child.R
import com.example.digitalsafety_child.databinding.ActivityCameraXactivityBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXActivity : AppCompatActivity() {

    val TAG: String = "CameraXApp"
    lateinit var binding: ActivityCameraXactivityBinding
    private var imageCapture: ImageCapture? = null
    private var outputDirectory: File? = null
    private var cameraExecutor: ExecutorService? = null
    val FILENAME_FORMAT: String = "yyyy-MM-dd-HH-mm-ss-SSS"
     val REQUEST_CODE_PERMISSIONS: Int = 20
    val REQUIRED_PERMISSIONS: Array<String> = arrayOf(Manifest.permission.CAMERA)
    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        wakeDevice(applicationContext)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun wakeDevice(context: Context) {
        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "MyApp::MyWakelockTag"
        )
        wakeLock.acquire(3000)

        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("MyApp::MyKeyguardLock")
        keyguardLock.disableKeyguard()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: Exception) {
                Log.e( TAG,
                    "Error getting camera provider",
                    e
                )
                return@addListener
            }

            // Preview
            val preview = Preview.Builder().build()

            preview.setSurfaceProvider(
                (findViewById<View>(R.id.viewFinder) as PreviewView).surfaceProvider
            )
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this@CameraXActivity, cameraSelector, preview, imageCapture
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    if (count < 2) takePhoto()
                }, 500)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        Log.d("Count---->", "$count inside take photo")
        val imageCapture = this.imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG,
                        "Photo capture failed: " + exc.message,
                        exc
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    // Set the saved uri to the image view
                    val imageView = findViewById<ImageView>(R.id.iv_capture)
                    imageView.visibility = View.VISIBLE
                    imageView.setImageURI(savedUri)

                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                    if (count < 2) {
                        count++
                        Log.d("Count---->", count.toString() + "incremented take photo")
                        takePhoto()
                    } else {
                        Log.d("Count---->", count.toString() + "close take photo")
                        closeCamera()
                    }
                }
            }
        )
    }


    private fun closeCamera() {
        Log.d("Count---->", count.toString() + "close take photo")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider
            try {
                cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing camera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun getOutputDirectory(): File {
        val mediaDirs = externalMediaDirs
        if (mediaDirs != null && mediaDirs.size > 0) {
            val mediaDir = File(mediaDirs[0], resources.getString(R.string.app_name))
            if (mediaDir.mkdirs() || mediaDir.exists()) {
                return mediaDir
            }
        }
        return filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
                Log.d("Permissions-->", "Permissions granted by the user")
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                Log.d("Permissions-->", "Permissions not granted by the user.")
                finish()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor!!.shutdown()
    }
}