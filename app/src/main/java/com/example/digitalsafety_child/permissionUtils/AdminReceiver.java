package com.example.digitalsafety_child.permissionUtils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import com.example.digitalsafety_child.activity.CameraXActivity;

import java.io.File;


public class AdminReceiver extends DeviceAdminReceiver {
    DevicePolicyManager devicePolicyManager;
    private int failedAttempts = 0;

    Context context;


    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "Enabled!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Toast.makeText(context, "Disabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        this.context = context;
        devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        failedAttempts = devicePolicyManager.getCurrentFailedPasswordAttempts();
        if (failedAttempts % 3 == 0) {
            System.out.println("Password Attempt is Failed 3..." + failedAttempts);
            Intent i = new Intent(context.getApplicationContext(), CameraXActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(i);

//            capturePhoto(context);


        } else {
            System.out.println("Password Attempt is Failed..." + failedAttempts);
        }
    }

    private void capturePhoto(Context context) {

//        CameraXConfig cameraXConfig = Camera2Config.defaultConfig();
//        CameraX.initialize(context, cameraXConfig);

        ImageCapture imageCapture = new ImageCapture.Builder().build();
        File photoFile = new File(context.getExternalFilesDir(null), "failed_unlock_attempt.jpg");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            imageCapture.takePicture(new ImageCapture.OutputFileOptions.Builder(photoFile).build(), context.getMainExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Log.d("CapturePhoto", "Photo captured successfully: " + photoFile.getAbsolutePath());
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e("CapturePhoto", "Error capturing photo: " + exception.getMessage());
                        }
                    });
        }
    }
}
