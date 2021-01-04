package com.simpelexo.alyfas5anyclient.ui.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.simpelexo.alyfas5anyclient.R;

public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);                // HelperMethods.replaceFragment(getActivity().getSupportFragmentManager(), R.id.splash_cycle_activity_frame, new SliderFragment());

        }, 3000);
    }
    }
