package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityCompressBinding;
import com.demo.zipextractor.databinding.DialogDeleteBinding;
import com.demo.zipextractor.databinding.DialogProgressBinding;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;
import com.demo.zipextractor.utils.ZipManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.lingala.zip4j.model.enums.CompressionLevel;

import org.apache.commons.io.FilenameUtils;


public class CompressActivity extends BaseActivity implements View.OnClickListener {
    String ROOT_PATH;
    ActivityCompressBinding binding;
    String compressionLevel;
    Dialog dialogDelete;
    DialogDeleteBinding dialogDeleteBinding;
    CompositeDisposable disposable;
    String format;
    String nameFile;
    String password;
    DialogProgressBinding progressBinding;
    Dialog progressDialog;
    ArrayList<File> selectedFileList;
    ArrayList<FileListModel> selectedFileListModel;
    boolean isFile = false;
    boolean isCancel = false;
    List<File> fileArrayList = new ArrayList();

    @Override
    public void setBinding() {
        this.binding = (ActivityCompressBinding) DataBindingUtil.setContentView(this, R.layout.activity_compress);
    }

    @Override
    public void setToolBar() {
        this.binding.toolbarCompress.title.setText("Compress");
        this.binding.toolbarCompress.cardBack.setOnClickListener(this);
    }

    @Override
    public void initMethod() {
        initView();
        initClick();
        this.progressBinding = (DialogProgressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_progress, null, false);
        this.progressDialog = new Dialog(this, R.style.dialogTheme);
        this.dialogDeleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        this.dialogDelete = new Dialog(this);
    }

    private void initClick() {
        this.binding.buttonnCompress.setOnClickListener(this);
        this.binding.checkBox.setOnClickListener(this);
        this.binding.ll.setOnClickListener(this);
        this.binding.showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CompressActivity.this.binding.edPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    CompressActivity.this.binding.showPass.setImageResource(R.drawable.invisible);
                    CompressActivity.this.binding.edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return;
                }
                CompressActivity.this.binding.showPass.setImageResource(R.drawable.visible);
                CompressActivity.this.binding.edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }


    public void showConfirmationDeleteDialog() {
        this.dialogDelete.setContentView(this.dialogDeleteBinding.getRoot());





        this.dialogDelete.getWindow().setLayout(-1, -2);
        this.dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialogDelete.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        this.dialogDelete.show();
        this.dialogDeleteBinding.msgDel.setText("Are you sure want to cancel Progress ?");
        this.dialogDeleteBinding.txtAction.setText("Okay");
        this.dialogDeleteBinding.llLine.setBackgroundColor(ContextCompat.getColor(this, R.color.fab_color));
        this.dialogDeleteBinding.cardRound.setCardBackgroundColor(ContextCompat.getColor(this, R.color.fab_color));
        this.dialogDeleteBinding.btnDelete.setCardBackgroundColor(ContextCompat.getColor(this, R.color.fab_color));
        this.dialogDeleteBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompressActivity.this.dialogDelete.cancel();
                CompressActivity.this.progressDialog.dismiss();
                CompressActivity.this.isCancel = true;
                CompressActivity.this.fileArrayList.clear();
                CompressActivity.this.setResult(0, CompressActivity.this.getIntent());
                CompressActivity.this.finish();
            }
        });
        this.dialogDeleteBinding.btnDelCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompressActivity.this.dialogDelete.cancel();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void initView() {
        this.disposable = new CompositeDisposable();
        this.selectedFileList = new ArrayList<>();
        this.selectedFileListModel = new ArrayList<>();
        this.ROOT_PATH = AppConstants.ROOT_PATH1;
        boolean booleanExtra = getIntent().getBooleanExtra("isFile", false);
        this.isFile = booleanExtra;
        if (booleanExtra) {
            this.selectedFileList = (ArrayList) getIntent().getSerializableExtra("selectedFileListModel");
        } else {
            this.selectedFileListModel = (ArrayList) getIntent().getSerializableExtra("selectedFileListModel");
        }
        @SuppressLint("ResourceType") ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367048, AppConstants.fileFormatList);
        arrayAdapter.setDropDownViewResource(17367049);
        this.binding.spinnerFormat.setAdapter((SpinnerAdapter) arrayAdapter);
        this.binding.spinnerFormat.setSelection(0);
        this.binding.spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, 17367048, AppConstants.compressLevelList);
        arrayAdapter2.setDropDownViewResource(17367049);
        this.binding.spinnerLevel.setAdapter((SpinnerAdapter) arrayAdapter2);
        this.binding.spinnerLevel.setSelection(0);
        this.binding.spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.binding.edSaveLocation.setText(this.ROOT_PATH);
        if (this.isFile) {
            if (this.selectedFileList.size() > 1) {
                this.binding.edFileName.setText("Internal Storage");
            } else {
                this.binding.edFileName.setText(FilenameUtils.getBaseName(this.selectedFileList.get(0).getName()));
            }
        } else if (this.selectedFileListModel.size() > 1) {
            this.binding.edFileName.setText("Internal Storage");
        } else {
            this.binding.edFileName.setText(FilenameUtils.getBaseName(this.selectedFileListModel.get(0).getFilename()));
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonnCompress:
                this.nameFile = this.binding.edFileName.getText().toString();
                this.format = this.binding.spinnerFormat.getSelectedItem().toString();
                this.compressionLevel = this.binding.spinnerLevel.getSelectedItem().toString();
                this.password = this.binding.edPassword.getText().toString();
                if (this.nameFile.isEmpty()) {
                    Toast.makeText(this, "Archive Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.progressDialog.setContentView(this.progressBinding.getRoot());
                this.progressDialog.show();
                Window window = this.progressDialog.getWindow();
                if (window != null) {
                    window.setLayout(-1, -2);
                    this.progressDialog.getWindow().setGravity(17);
                    this.progressDialog.setCancelable(false);
                    this.progressDialog.setCanceledOnTouchOutside(false);
                    this.progressDialog.getWindow().setBackgroundDrawableResource(17170445);
                    this.progressDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                }
                Glide.with((FragmentActivity) this).load(Integer.valueOf((int) R.raw.compress)).into(this.progressBinding.imgLoader);
                this.progressBinding.txtFileName.setText(this.nameFile);
                this.progressBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        CompressActivity.this.showConfirmationDeleteDialog();
                    }
                });
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return CompressActivity.this.m87x919d52d4();
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
                        CompressActivity.this.progressDialog.cancel();
                        CompressActivity.this.setResult(-1, CompressActivity.this.getIntent());
                        CompressActivity.this.finish();
                        if (CompressActivity.this.isCancel) {
                            new File(CompressActivity.this.ROOT_PATH + CompressActivity.this.nameFile + CompressActivity.this.format).delete();
                        }
                    }
                }));
                return;
            case R.id.cardBack:
                onBackPressed();
                return;
            case R.id.checkBox:
                if (this.binding.checkBox.isChecked()) {
                    this.binding.layoutSetPassword.setVisibility(View.VISIBLE);
                    return;
                } else {
                    this.binding.layoutSetPassword.setVisibility(View.GONE);
                    return;
                }
            case R.id.ll:
                Intent intent = new Intent(this, SaveLocationActivity.class);
                intent.putExtra("comeFrom", "Save");
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        CompressActivity.this.m86x462a153((ActivityResult) obj);
                    }
                });
                return;
            default:
                return;
        }
    }


    public void m86x462a153(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.binding.edSaveLocation.setText(activityResult.getData().getStringExtra("path"));
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
    }


    public Boolean m87x919d52d4() {
        if (this.isFile) {
            zipFileFolderFromFile();
        } else {
            zipFileFoldersFromModel();
        }
        return true;
    }

    private void zipFileFolderFromFile() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            this.fileArrayList.add(new File(this.selectedFileList.get(i).getAbsolutePath()));
        }
        if (this.password.isEmpty()) {
            Log.e("pass", "zipFileFolders: " + this.ROOT_PATH);
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FAST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FAST, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FASTEST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FASTEST, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.MAXIMUM_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.MAXIMUM, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.ULTRA_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.ULTRA, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.STORE_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.HIGHER, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        }
        AppConstants.refreshGallery(this.ROOT_PATH + this.nameFile + this.format, this);
        this.binding.rlProgess.setVisibility(View.GONE);
    }

    private void zipFileFoldersFromModel() {
        for (int i = 0; i < this.selectedFileListModel.size(); i++) {
            this.fileArrayList.add(new File(this.selectedFileListModel.get(i).getFilePath()));
        }
        if (this.password.isEmpty()) {
            Log.e("pass", "zipFileFolders: " + this.ROOT_PATH);
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FAST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FAST, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FASTEST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FASTEST, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.MAXIMUM_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.MAXIMUM, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.ULTRA_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.ULTRA, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.STORE_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.HIGHER, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        } else {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, this.isCancel);
        }
        AppConstants.refreshGallery(this.ROOT_PATH + this.nameFile + this.format, this);
        this.binding.rlProgess.setVisibility(View.GONE);
    }
}
