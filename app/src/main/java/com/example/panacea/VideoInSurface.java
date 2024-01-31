package com.example.panacea;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.HandlerThread;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.Objects;

public class VideoInSurface {
    static final int REQUEST_CAMERA_PERMISSION = 1;
    private final Context context;
    private String cameraID;
    private CameraDevice finalCameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder requestBuilder;

    VideoInSurface(Context context) {
        this.context = context;
    }
    void startVideo(Surface surface) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraID = Objects.requireNonNull(cameraManager).getCameraIdList()[0];
        } catch (Exception e) {
            Toast.makeText(context, "Unable to access the camera", Toast.LENGTH_LONG).show();
        }
        try {
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permissions are required to access the camera", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            if(cameraID == null) {
                return;
            }
            Objects.requireNonNull(cameraManager).openCamera(cameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    finalCameraDevice = cameraDevice;
                    CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            try {
                                requestBuilder = finalCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                requestBuilder.addTarget(surface);
                                requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                                HandlerThread thread = new HandlerThread("CameraView");
                                thread.start();
                                cameraCaptureSession.setRepeatingRequest(requestBuilder.build(), null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        }
                    };

                    try {
                        finalCameraDevice.createCaptureSession(Collections.singletonList(surface), stateCallback, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                }
                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void stopCamera() {
        try {
            Toast.makeText(context, "Stopped recording. You can remove your finger", Toast.LENGTH_LONG).show();
            finalCameraDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

