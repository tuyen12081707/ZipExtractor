package com.demo.zipextractor.utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    public abstract void initMethod();

    public abstract void setBinding();

    public abstract void setToolBar();


    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setBinding();
        setToolBar();
        initMethod();
    }
}
