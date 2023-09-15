package com.era.app.launcher;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.era.app.R;
import com.era.app.launcher.home.ActivityHome;
import com.era.app.utils.Utils;
import com.era.app.utils.dialog.ErrorExitDialog;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityStart extends AppCompatActivity {
    private final Handler toastHandler = new Handler();
    private boolean doubleBack = false;
    private TextView txtLoadingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        txtLoadingInfo = findViewById(R.id.loading);

        new Thread(this::loadResource).start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (doubleBack) {
            finish();
            return;
        }

        doubleBack = true;
        Toast.makeText(this, R.string.msg_double_press_back, Toast.LENGTH_SHORT).show();
        toastHandler.postDelayed(() -> doubleBack = false, Utils.DURATION_SHORT);
    }

    private void updateLoadingInfo(CharSequence info) {
        runOnUiThread(() -> txtLoadingInfo.setText(info));
    }

    private void loadResource() {
        // Load done
        try {
            updateLoadingInfo("Still loading");
//            Thread.sleep(5000);
            updateLoadingInfo("Load successfully");

            Thread.sleep(1000);
            startActivity(new Intent(this, ActivityHome.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Utils.runInDebug(e::printStackTrace);
            new ErrorExitDialog("Exit ERA application", "Error when loading resource").show(getSupportFragmentManager(), null);
        }
    }
}
