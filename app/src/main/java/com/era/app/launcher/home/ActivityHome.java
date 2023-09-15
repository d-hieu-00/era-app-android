package com.era.app.launcher.home;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.era.app.R;
import com.era.app.utils.Utils;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ActivityHome extends AppCompatActivity {
    private ImageView imgTest;
    private final ActivityResultCallback<Boolean> requestPermissionCallback = this::openImage;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), requestPermissionCallback);
    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::displayImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgTest = findViewById(R.id.img_test);
        Button btnTest = findViewById(R.id.btn_test);

        btnTest.setOnClickListener(view -> openImage(false));

        if (OpenCVLoader.initDebug()) {
            Log.d("ActivityHome", "LOADED OPENCV");
        } else {
            Log.e("ActivityHome", "FAILED TO LOAD OPENCV");
        }
    }

    protected void displayImage(Uri uri) {
        if (uri != null) {
//            Log.i("PhotoPicker", "Selected URI: " + uri);
            Bitmap selectedBitmap;
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//                Imgproc.map
//                Size s = new Size(selectedBitmap.getWidth(),selectedBitmap.getHeight())
//                Mat mat = Imgcodecs.imread(uri.toString());
                Mat mat = new Mat();
                org.opencv.android.Utils.bitmapToMat(selectedBitmap, mat);
                Mat matOut = new Mat();
                Imgproc.GaussianBlur(mat, matOut, new Size(0,0), 10,10);

//                Mat matOut2 = new Mat();
//                org.opencv.photo.Photo.stylization(matOut, matOut2, 150, 0.25F);

                Bitmap bitmap = Bitmap.createBitmap(matOut.cols(), matOut.rows(), Bitmap.Config.ARGB_8888);
                org.opencv.android.Utils.matToBitmap(matOut, bitmap);

                Log.e("TEST", uri.toString());
                imgTest.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(this, "No media image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("InlinedApi")
    protected void openImage(Boolean hasPermission) {
        if (!hasPermission) {
            Utils.getPermission(this, requestPermissionLauncher, getString(R.string.msg_permission_read_media_image_reason), permission.READ_MEDIA_IMAGES, requestPermissionCallback);
            return;
        }

        pickImageLauncher.launch(new PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
            .build());
    }
}

