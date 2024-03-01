package com.demo.zipextractor.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;


import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivitySplashBinding;
import com.demo.zipextractor.utils.AppPref;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Context context;
    SplashActivity splash_activity;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.binding = (ActivitySplashBinding) DataBindingUtil.setContentView(this, R.layout.activity_splash);
        this.context = this;
        this.splash_activity = this;
        Glide.with(this.context).load(Integer.valueOf((int) R.raw.splash_img)).into(this.binding.img);
        initView();
    }
    private void initView() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                GoToMainScreen();
            }
        }, 2000L);
    }
    public void GoToMainScreen() {
        startActivity(new Intent(this.context, HomeActivity.class));
        finish();
    }
}
