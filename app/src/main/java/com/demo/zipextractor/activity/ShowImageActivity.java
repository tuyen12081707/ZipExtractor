package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;
import com.demo.zipextractor.utils.ZipManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityShowImageBinding;
import com.demo.zipextractor.databinding.BottomsheetlayoutMultipleBinding;
import com.demo.zipextractor.databinding.DialogCompressBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;


public class ShowImageActivity extends BaseActivity implements View.OnClickListener {
    ActivityShowImageBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    String compressionLevel;
    Context context;
    Dialog dialogCompress;
    DialogCompressBinding dialogCompressBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    CompositeDisposable disposable;
    FileListModel fileListModel;
    String format;
    boolean isDelete = false;
    String nameFile;
    String password;

    @Override
    public void setBinding() {
        this.binding = (ActivityShowImageBinding) DataBindingUtil.setContentView(this, R.layout.activity_show_image);


        this.fileListModel = (FileListModel) getIntent().getParcelableExtra("filelistModel");
        this.context = this;
        this.disposable = new CompositeDisposable();
        this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheetlayout_multiple, null, false);
        this.dialogMultiBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.dialogCompressBinding = (DialogCompressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_compress, null, false);
        this.dialogCompress = new Dialog(this);
    }

    @Override
    public void setToolBar() {
        this.binding.toolbarShowImage.title.setText(this.fileListModel.getFilename());
        this.binding.toolbarShowImage.cardMenu.setVisibility(View.VISIBLE);
        this.binding.toolbarShowImage.cardMenu.setOnClickListener(this);
        this.binding.toolbarShowImage.cardBack.setOnClickListener(this);
    }

    @Override
    public void initMethod() {
        setImage();
        initClick();
    }

    private void setImage() {
        Glide.with((FragmentActivity) this).load(this.fileListModel.getFilePath()).into(this.binding.ivShowImage);
    }

    private void initClick() {
        this.bottomsheetlayoutMultipleBinding.layoutCopy.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMove.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiDelete.setOnClickListener(this);
        this.dialogCompressBinding.buttonnCompress.setOnClickListener(this);
        this.dialogCompressBinding.buttonnCancel.setOnClickListener(this);
        this.dialogCompressBinding.checkBox.setOnClickListener(this);
        this.dialogCompressBinding.ll.setOnClickListener(this);
    }

    private void ShowMultiBottomSheetDialog() {
        this.dialogMultiBottomSheet.setContentView(this.bottomsheetlayoutMultipleBinding.getRoot());



        this.dialogMultiBottomSheet.show();
        this.bottomsheetlayoutMultipleBinding.tvCompress.setText("Compressed to *.zip");
        this.bottomsheetlayoutMultipleBinding.tvCompressDialog.setText("Compressed");
        this.bottomsheetlayoutMultipleBinding.tvOpenWith.setText("Open With...");
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.GONE);
        this.bottomsheetlayoutMultipleBinding.tvSortTitle.setText(this.fileListModel.getFilename());
    }

    @SuppressLint("ResourceType")
    private void openCompressDialog() {
        this.dialogCompress.setContentView(this.dialogCompressBinding.getRoot());
        Window window = this.dialogCompress.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
        }
        this.dialogCompress.setCanceledOnTouchOutside(true);
        this.dialogCompress.show();
        @SuppressLint("ResourceType") ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367048, AppConstants.fileFormatList);
        arrayAdapter.setDropDownViewResource(17367049);
        this.dialogCompressBinding.spinnerFormat.setAdapter((SpinnerAdapter) arrayAdapter);
        this.dialogCompressBinding.spinnerFormat.setSelection(0);
        this.dialogCompressBinding.spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, 17367048, AppConstants.compressLevelList);
        arrayAdapter2.setDropDownViewResource(17367049);
        this.dialogCompressBinding.spinnerLevel.setAdapter((SpinnerAdapter) arrayAdapter2);
        this.dialogCompressBinding.spinnerLevel.setSelection(0);
        this.dialogCompressBinding.spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.format = this.dialogCompressBinding.spinnerFormat.getSelectedItem().toString();
        this.password = this.dialogCompressBinding.edPassword.getText().toString();
        this.compressionLevel = this.dialogCompressBinding.spinnerLevel.getSelectedItem().toString();
        this.nameFile = FilenameUtils.getBaseName(this.fileListModel.getFilename());
        this.dialogCompressBinding.edFileName.setText(this.nameFile);
        this.dialogCompressBinding.edSaveLocation.setText(this.fileListModel.getFilePath());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cardBack) {
            onBackPressed();
        } else if (id == R.id.cardMenu) {
            ShowMultiBottomSheetDialog();
        } else if (id == R.id.layoutCopy) {
            this.binding.rlProgess.setVisibility(View.VISIBLE);
            this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                @Override
                public final Object call() {
                    return ShowImageActivity.this.m115x3e6850b();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
                @Override
                public void onError(Throwable th) {
                }

                @Override
                public void onNext(Boolean bool) {
                }

                @Override
                public void onComplete() {
                    ShowImageActivity.this.dialogMultiBottomSheet.dismiss();
                    ShowImageActivity.this.binding.rlProgess.setVisibility(View.GONE);
                    ShowImageActivity.this.onBackPressed();
                    Toast.makeText(ShowImageActivity.this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
                }
            }));
        } else {
            switch (id) {
                case R.id.layoutInfo:
                    AppConstants.openWith(this, Uri.fromFile(new File(this.fileListModel.getFilePath())), "image/*");
                    return;
                case R.id.layoutMove:
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(this.fileListModel);
                    Intent intent = new Intent(this, CompressActivity.class);
                    intent.putExtra("selectedFileListModel", arrayList);
                    this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                        @Override

                        public final void onActivityResult(Object obj) {
                            ShowImageActivity.this.m116x1e0203aa((ActivityResult) obj);
                        }
                    });
                    return;
                case R.id.layoutMultiDelete:
                    File file = new File(this.fileListModel.getFilePath());
                    file.delete();
                    AppConstants.refreshGallery(file.getPath(), this.context);
                    this.isDelete = true;
                    onBackPressed();
                    return;
                case R.id.layoutMultiShare:
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(this.fileListModel.getFilePath());
                    AppConstants.share(this, arrayList2);
                    this.dialogMultiBottomSheet.dismiss();
                    return;
                default:
                    return;
            }
        }
    }


    public Boolean m115x3e6850b() {
        File file = new File(this.fileListModel.getFilePath());
        try {
            file.createNewFile();
            File file2 = new File(file, "");
            String[] strArr = new String[1];
            file2.getAbsolutePath();
            ZipManager.zipSingle(new File(this.fileListModel.getFilePath()), this.fileListModel.getFilePath() + AppConstants.ZIP_FORMAT);
            AppConstants.refreshGallery(this.fileListModel.getFilePath() + AppConstants.ZIP_FORMAT, this.context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public void m116x1e0203aa(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Toast.makeText(this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("fileListModel", this.fileListModel);
        intent.putExtra("isDelete", this.isDelete);
        setResult(-1, intent);
        finish();
    }
}
