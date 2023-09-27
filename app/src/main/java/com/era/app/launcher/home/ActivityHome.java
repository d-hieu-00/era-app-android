package com.era.app.launcher.home;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest.permission;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.era.app.R;
import com.era.app.utils.Utils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ActivityHome extends AppCompatActivity {
    protected Boolean hasPermission = false;
    protected JavaCameraView cameraView;
    protected ImageView imageView;
    protected Bitmap currentBitmapObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnTest = findViewById(R.id.btn_test);
        cameraView = findViewById(R.id.camera_view);
        imageView = findViewById(R.id.img_view);
        imageView.setVisibility(View.GONE);

        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {}
            @Override
            public void onCameraViewStopped() {}
            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                Mat inMat = inputFrame.rgba();
//                Mat rotatedFrame = new Mat(inMat.rows(), inMat.cols(), inMat.type());
//                Core.rotate(inMat, rotatedFrame, Core.ROTATE_90_CLOCKWISE);
//                return rotatedFrame;
                Mat mat1 = new Mat();
                Mat mat2 = new Mat();

                Core.rotate(inMat, mat1, Core.ROTATE_90_CLOCKWISE);
                Imgproc.resize(mat1, mat2, inMat.size());

                mat1.release();
                inMat.release();

//                currentMatObj = new
                currentBitmapObj = Bitmap.createBitmap(mat2.cols(), mat2.rows(), Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(mat2, currentBitmapObj);

                return mat2;
            }
        });

        btnTest.setOnClickListener(view -> captureFn());

        if (OpenCVLoader.initDebug()) {
            Log.d("ActivityHome", "LOADED OPENCV");
        } else {
            Log.e("ActivityHome", "FAILED TO LOAD OPENCV");
        }
    }

    protected void captureFn() {
        if (!hasPermission) {
            return;
        }
        cameraView.disableView();
        cameraView.setVisibility(View.GONE);

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(currentBitmapObj);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityResultCallback<Boolean> launcherCallback = (ok) -> {
            hasPermission = ok;
            if (ok) {
                cameraView.enableView();
                Log.i("Home", "Granted camera permission");
            } else {
                Toast.makeText(this, "Failed to get camera permission", Toast.LENGTH_SHORT).show();
            }
        };

        ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), launcherCallback);
        Utils.getPermission(this, permissionLauncher, getString(R.string.msg_permission_camera), permission.CAMERA, launcherCallback);
    }

}

