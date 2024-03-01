package com.demo.zipextractor.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivitySettingsBinding;
import com.demo.zipextractor.utils.AppPref;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingsBinding binding;
    Context context;


    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.binding = (ActivitySettingsBinding) DataBindingUtil.setContentView(this, R.layout.activity_settings);
        this.context = this;
        InitView();
        setOnClick();
    }

    private void InitView() {
        this.binding.switchHide.setChecked(AppPref.IsHidden(this.context));
        this.binding.switchThumb.setChecked(AppPref.IsThumbnail(this.context));
        this.binding.switchHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingsActivity.this.m113x8c8e89ba(compoundButton, z);
            }
        });
        this.binding.switchThumb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingsActivity.this.m114x19c93b3b(compoundButton, z);
            }
        });
    }


    public void m113x8c8e89ba(CompoundButton compoundButton, boolean z) {
        AppPref.setIsHidden(this.context, z);
    }


    public void m114x19c93b3b(CompoundButton compoundButton, boolean z) {
        AppPref.setIsThumbnail(this.context, z);
    }

    private void setOnClick() {
        this.binding.cardBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.card_back) {
            onBackPressed();
        }
    }

}
