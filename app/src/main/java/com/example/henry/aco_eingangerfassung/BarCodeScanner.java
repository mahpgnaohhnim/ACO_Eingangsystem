package com.example.henry.aco_eingangerfassung;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.Manifest;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;



/**
 * Created by henry on 12.02.17.
 */

public class BarCodeScanner extends AppCompatActivity {

    SurfaceView camPreview;
    Button backToMainBtn;
    BarcodeDetector detector;
    CameraSource camSource;
    SurfaceHolder holder;

    private static final String TAG = "Barcode-reader";
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.scanner_activity);

        backToMainBtn = (Button) findViewById(R.id.backToMainBtn);
        camPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        camPreview.setZOrderMediaOverlay(true);

        holder = camPreview.getHolder();
        detector = new BarcodeDetector.Builder(this).build();

        camSource = new CameraSource.Builder(this, detector).setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true).setRequestedFps(24).setRequestedPreviewSize(1920, 1024).build();

        camPreview.getHolder().addCallback(new SurfaceHolder.Callback(){

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(BarCodeScanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestCameraPermission();
                    }
                    camSource.start(camPreview.getHolder());
                }catch(java.io.IOException e) {
                    Log.d("Error: ", e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() > 0){
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        backToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
    }
}
