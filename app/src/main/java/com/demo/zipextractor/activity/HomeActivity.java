package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;


import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityHomeBinding;
import com.demo.zipextractor.databinding.DialogPermissionsBinding;
import com.demo.zipextractor.model.SDCardInfoModel;
import com.demo.zipextractor.model.StorageUtils;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;

import java.lang.ref.WeakReference;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final int READ_STORAGE_PERMISSION_REQUEST = 123;

    private static Context context;

    String PlayStoreUrl;
    ActivityHomeBinding binding;
    HomeActivity mainActivity;
    WeakReference<HomeActivity> mainActivityWeakReference;

    boolean isPermissionGranted = false;
    boolean permissionNotify = false;

    @Override
    public void setToolBar() {
    }

    @Override
    public void setBinding() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(getResources().getColor(R.color.main_bg));
        }
        this.binding = (ActivityHomeBinding) DataBindingUtil.setContentView(this, R.layout.activity_home);



        this.PlayStoreUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
        this.mainActivity = this;
        context = this;
        this.mainActivityWeakReference = new WeakReference<>(this.mainActivity);

    }

    @Override
    public void initMethod() {
        readWritePermission();
        getSize();
        this.binding.includeMain.cardInternalStorage.setOnClickListener(this);
        this.binding.includeMain.cardDoc.setOnClickListener(this);
        this.binding.includeMain.cardArchive.setOnClickListener(this);
        this.binding.includeMain.cardVideo.setOnClickListener(this);
        this.binding.includeMain.cardAudio.setOnClickListener(this);
        this.binding.includeMain.cardApk.setOnClickListener(this);
        this.binding.includeMain.cardPicture.setOnClickListener(this);
        this.binding.navdrawer.cardClose.setOnClickListener(this);
        this.binding.navdrawer.cardPro.setOnClickListener(this);
        this.binding.navdrawer.cardRate.setOnClickListener(this);
        this.binding.navdrawer.cardSettings.setOnClickListener(this);
        this.binding.navdrawer.llPolicy.setOnClickListener(this);
        this.binding.navdrawer.llShare.setOnClickListener(this);
        this.binding.navdrawer.llRate.setOnClickListener(this);
        this.binding.navdrawer.dialogRatingRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public final void onRatingChanged(RatingBar ratingBar, float f, boolean z) {
                HomeActivity.this.m100x2f09d652(ratingBar, f, z);
            }
        });
        this.binding.includeMain.drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                HomeActivity.this.m101x2e937053(view);
            }
        });
    }


    public void m100x2f09d652(RatingBar ratingBar, float f, boolean z) {
        openPlaystore();
    }


    public void m101x2e937053(View view) {
        if (this.binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            this.binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            this.binding.drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    private void readWritePermission() {
        if (Build.VERSION.SDK_INT >= 30) {
            this.permissionNotify = AppConstants.checkStoragePermissionApi30(this);
        } else {
            this.permissionNotify = AppConstants.checkStoragePermissionApi19(this);
        }
        if (this.permissionNotify) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 30) {
            openDialogPermission();
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 1001);
        }
    }

    @SuppressLint("ResourceType")
    private void openDialogPermission() {
        DialogPermissionsBinding dialogPermissionsBinding = (DialogPermissionsBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_permissions, null, false);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogPermissionsBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(17170445);
        dialog.getWindow().setLayout(-1, -2);
        dialog.show();
        Glide.with((FragmentActivity) this).load(Integer.valueOf((int) R.raw.permission_switch)).into(dialogPermissionsBinding.imgPermission);
        dialogPermissionsBinding.cardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HomeActivity.this.showPermissionNotifyDialog();
            }
        });
        dialogPermissionsBinding.cardCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HomeActivity.this.finish();
            }
        });
    }

    public void showPermissionNotifyDialog() {
        if (Build.VERSION.SDK_INT >= 30) {
            this.activityLauncher.launch(new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION", Uri.parse("package:"+getPackageName())), new BetterActivityResult.OnActivityResult() {
                @Override
                public final void onActivityResult(Object obj) {
                    HomeActivity.this.m102x3499d400((ActivityResult) obj);
                }
            });
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 1001);
        }
    }


    public void m102x3499d400(ActivityResult activityResult) {
        if (AppConstants.checkStoragePermissionApi30(this)) {
            return;
        }
        openDialogPermission();
    }

    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
                return;
            }
            this.isPermissionGranted = true;
            return;
        }
        String[] strArr = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(this, strArr)) {
            this.isPermissionGranted = true;
        } else {
            EasyPermissions.requestPermissions(this, "Our App Requires a permission to access your storage", 123, strArr);
        }
    }

    private void getSize() {
        SDCardInfoModel systemSpaceinfo = StorageUtils.getSystemSpaceinfo(this, Environment.getExternalStorageDirectory().getPath());
        long free = systemSpaceinfo.free;
        long total = systemSpaceinfo.total;
        long storeSpace = total - free;
        this.binding.includeMain.totalSize.setText(AppConstants.convertStorage(storeSpace) + " / " + AppConstants.convertStorage(total) + "");

        Log.e("MYTAG", "ErrorNo: storeSpace:" +storeSpace);
        Log.e("MYTAG", "ErrorNo: total:" +total);
        Log.e("MYTAG", "ErrorNo: getSize1:" +(float)((storeSpace / total) * 100.0d));
        Log.e("MYTAG", "ErrorNo: getSize2:" +(float)((storeSpace / total) * 100.0d)+1.0d);
        int i = (int) ((float)((float)((float)storeSpace / total) * 100.0d) + 1.0d);
        this.binding.includeMain.circularProgressIndicator.setProgressCompat(i, true);
        this.binding.includeMain.tvProgress.setText("" + i + "%");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardApk:
                startActivity(new Intent(this, ApkFilesActivity.class));
                return;
            case R.id.cardArchive:
                startActivity(new Intent(this, ArchiveActivity.class));
                return;
            case R.id.cardAudio:
                startActivity(new Intent(this, AudioActivity.class));
                return;
            case R.id.cardDoc:
                startActivity(new Intent(this, DocumentActivity.class));
                return;
            case R.id.cardInternalStorage:
                startActivity(new Intent(this, AllFilesActivity.class));
                return;
            case R.id.cardPicture:
                startActivity(new Intent(this, PictureActivity.class));
                return;
            case R.id.cardVideo:
                startActivity(new Intent(this, VideoActivity.class));
                return;
            case R.id.card_close:
                this.binding.drawerLayout.closeDrawers();
                return;
            case R.id.card_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
            case R.id.ll_policy:
                AppConstants.openUrl(this, AppConstants.PRIVACY_POLICY_URL);
                return;
            case R.id.ll_share:
                AppConstants.shareapp(this);
                return;
            case R.id.ll_rate:
                String packageName = getPackageName();
                try {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                    intent.setPackage("com.android.vending");
                   startActivity(intent);
                } catch (Exception unused) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }

                return;
            default:
                return;
        }
    }

    private void openPlaystore() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.PlayStoreUrl)));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this, "Couldn't find PlayStore on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {
        if (Build.VERSION.SDK_INT >= 30) {
            Intent intent = new Intent();
            intent.setAction("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
            if (list.size() > 1) {
                this.isPermissionGranted = true;
            }
        }
    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            this.binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }
}
