package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.zipextractor.R;
import com.demo.zipextractor.adapter.AllFileAdapter;
import com.demo.zipextractor.adapter.NavPathAdapter;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.utils.ZipManager;
import com.demo.zipextractor.databinding.ActivityShowZipContentBinding;
import com.demo.zipextractor.databinding.DialogPassuncomressBinding;

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

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FilenameUtils;


public class ShowZipContentActivity extends BaseActivity implements View.OnClickListener, RecyclerItemClick, AllFileAdapter.FragmentCommunication, SwipeRefreshLayout.OnRefreshListener {
    AllFileAdapter adapter;
    NavPathAdapter adapterNav;
    ActivityShowZipContentBinding binding;
    Dialog dialogPassUncompress;
    DialogPassuncomressBinding dialogPassuncomressBinding;
    CompositeDisposable disposable;
    FileListModel fileListModel;
    File tempDir;
    String unzipLocation;
    File zipFile;
    ArrayList<String> navPathList = new ArrayList<>();
    String rootPath = "";
    ArrayList<File> fileList = new ArrayList<>();
    boolean isComeFromArchive = false;

    @Override
    public void setBinding() {
        this.binding = (ActivityShowZipContentBinding) DataBindingUtil.setContentView(this, R.layout.activity_show_zip_content);
        this.dialogPassuncomressBinding = (DialogPassuncomressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_passuncomress, null, false);
        this.dialogPassUncompress = new Dialog(this, R.style.dialogTheme);
        this.fileListModel = new FileListModel();
        boolean booleanExtra = getIntent().getBooleanExtra("isComeArchive", false);
        this.isComeFromArchive = booleanExtra;
        if (!booleanExtra) {
            this.zipFile = (File) getIntent().getSerializableExtra("zipFileName");
            this.tempDir = new File(getCacheDir(), "temp/" + FilenameUtils.getBaseName(this.zipFile.getPath()));
        } else {
            this.fileListModel = (FileListModel) getIntent().getParcelableExtra("modelFileList");
            this.tempDir = new File(getCacheDir(), "temp/" + FilenameUtils.getBaseName(this.fileListModel.getFilePath()));
        }
        if (!this.tempDir.exists()) {
            this.tempDir.mkdir();
        }
        this.dialogPassuncomressBinding.showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShowZipContentActivity.this.dialogPassuncomressBinding.edShowPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    ShowZipContentActivity.this.dialogPassuncomressBinding.showPass.setImageResource(R.drawable.invisible);
                    ShowZipContentActivity.this.dialogPassuncomressBinding.edShowPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return;
                }
                ShowZipContentActivity.this.dialogPassuncomressBinding.showPass.setImageResource(R.drawable.visible);
                ShowZipContentActivity.this.dialogPassuncomressBinding.edShowPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    @Override
    public void setToolBar() {
        this.binding.toolBarShowZipContent.cardBack.setOnClickListener(this);
        this.binding.swipeToRefresh.setOnRefreshListener(this);
        this.dialogPassuncomressBinding.buttonnShowCancel.setOnClickListener(this);
        this.dialogPassuncomressBinding.buttonnShowCompress.setOnClickListener(this);
        if (!this.isComeFromArchive) {
            this.binding.toolBarShowZipContent.title.setText(this.zipFile.getName());
            this.navPathList.add(0, this.zipFile.getName());
            return;
        }
        this.binding.toolBarShowZipContent.title.setText(this.fileListModel.getFilename());
        this.navPathList.add(0, this.fileListModel.getFilename());
    }

    @Override
    public void initMethod() {
        this.disposable = new CompositeDisposable();
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
            @Override
            public final Object call() {
                return ShowZipContentActivity.this.m117x25ff6368();
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
                try {
                    if (!ShowZipContentActivity.this.isComeFromArchive) {
                        if (!new ZipFile(ShowZipContentActivity.this.zipFile.getPath()).isEncrypted()) {
                            ShowZipContentActivity.this.setAdapter();
                            ShowZipContentActivity.this.setAdapternavPath();
                        }
                    } else if (!new ZipFile(ShowZipContentActivity.this.fileListModel.getFilePath()).isEncrypted()) {
                        ShowZipContentActivity.this.setAdapter();
                        ShowZipContentActivity.this.setAdapternavPath();
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                ShowZipContentActivity.this.binding.rlProgess.setVisibility(View.GONE);
            }
        }));
    }


    public Boolean m117x25ff6368() {
        try {
            if (!this.isComeFromArchive) {
                if (new ZipFile(this.zipFile.getPath()).isEncrypted()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowZipContentActivity showZipContentActivity = ShowZipContentActivity.this;
                            showZipContentActivity.rootPath = FilenameUtils.removeExtension(showZipContentActivity.tempDir.getPath());
                            ShowZipContentActivity.this.openUnzipDialog();
                        }
                    });
                } else {
                    ZipManager.unzip(this.zipFile.getAbsolutePath(), FilenameUtils.removeExtension(this.tempDir.getAbsolutePath()));
                }
                String removeExtension = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getAbsolutePath()));
                this.rootPath = removeExtension;
                this.fileList = AppConstants.getAllFile(removeExtension);
            } else if (new ZipFile(this.fileListModel.getFilePath()).isEncrypted()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShowZipContentActivity showZipContentActivity = ShowZipContentActivity.this;
                        showZipContentActivity.rootPath = FilenameUtils.removeExtension(showZipContentActivity.tempDir.getPath());
                        ShowZipContentActivity.this.openUnzipDialog();
                    }
                });
            } else {
                new ZipFile(this.fileListModel.getFilePath()).extractAll(FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath())));
                Log.e("fileList", "1==>");
                this.rootPath = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()));
                Log.e("fileList", "2==>");
                this.fileList = AppConstants.getAllFile(this.rootPath);
                Log.e("fileList", this.fileList.size() + "3==>" + this.rootPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    @SuppressLint("ResourceType")
    public void openUnzipDialog() {
        this.dialogPassUncompress.setContentView(this.dialogPassuncomressBinding.getRoot());
        Window window = this.dialogPassUncompress.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            this.dialogPassUncompress.getWindow().setGravity(17);
            this.dialogPassUncompress.getWindow().setBackgroundDrawableResource(17170445);
            this.dialogPassUncompress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        this.dialogPassUncompress.setCanceledOnTouchOutside(true);
        this.dialogPassUncompress.show();
    }


    public void setAdapter() {
        ArrayList<File> arrayList = this.fileList;
        this.adapter = new AllFileAdapter(this, arrayList, arrayList, null, this, true);
        this.binding.rvAllFile.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvAllFile.setAdapter(this.adapter);
        this.binding.rvAllFile.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.item_animation));
    }


    public void setAdapternavPath() {
        this.adapterNav = new NavPathAdapter(this, this.navPathList, this);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.recyclerView.setAdapter(this.adapterNav);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonnShowCancel:
                this.dialogPassUncompress.dismiss();
                onBackPressed();
                return;
            case R.id.buttonnShowCompress:
                this.dialogPassUncompress.dismiss();
                this.binding.rlProgess.setVisibility(View.VISIBLE);
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return ShowZipContentActivity.this.m119x694aafe8();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean bool) {
                    }

                    @Override
                    public void onError(Throwable th) {
                        Log.e("ERR", "onError: " + th.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        ShowZipContentActivity.this.binding.rlProgess.setVisibility(View.GONE);
                        ShowZipContentActivity showZipContentActivity = ShowZipContentActivity.this;
                        showZipContentActivity.fileList = AppConstants.getAllFile(showZipContentActivity.rootPath);
                        ShowZipContentActivity.this.setAdapter();
                        ShowZipContentActivity.this.setAdapternavPath();
                    }
                }));
                return;
            case R.id.cardBack:
                onBackPressed();
                return;
            default:
                return;
        }
    }


    public Boolean m119x694aafe8() {
        String filePath;
        try {
            if (!this.isComeFromArchive) {
                filePath = this.zipFile.getPath();
                this.unzipLocation = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()));
            } else {
                filePath = this.fileListModel.getFilePath();
                this.unzipLocation = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()));
            }
            ZipFile zipFile = new ZipFile(filePath, this.dialogPassuncomressBinding.edShowPassword.getText().toString().toCharArray());
            zipFile.extractAll(this.unzipLocation);
            try {
                zipFile.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.dialogPassUncompress.dismiss();
        } catch (ZipException e) {
            if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public final void run() {
                        ShowZipContentActivity.this.m118x5894e327();
                    }
                });
                e.printStackTrace();
            }
        }
        return true;
    }


    public void m118x5894e327() {
        try {
            ZipManager.doDelete(new File(this.unzipLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
        this.dialogPassUncompress.show();
    }

    @Override
    public void onRecyclerClick(int i) {
        if (i > 0) {
            ArrayList<String> arrayList = this.navPathList;
            arrayList.subList(i + 1, arrayList.size()).clear();
            this.rootPath = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()) + InternalZipConstants.ZIP_FILE_SEPARATOR);
            for (int i2 = 1; i2 < this.navPathList.size(); i2++) {
                this.rootPath += this.navPathList.get(i2) + InternalZipConstants.ZIP_FILE_SEPARATOR;
            }
        } else {
            this.navPathList.clear();
            if (!this.isComeFromArchive) {
                this.rootPath = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()));
                this.navPathList.add(0, this.zipFile.getName());
            } else {
                this.rootPath = FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.tempDir.getPath()));
                this.navPathList.add(0, this.fileListModel.getFilename());
            }
        }
        ArrayList<File> allFile = AppConstants.getAllFile(this.rootPath);
        this.fileList = allFile;
        this.adapter.setList(allFile);
    }

    @Override
    public void respond(String str) {
        this.rootPath = str + InternalZipConstants.ZIP_FILE_SEPARATOR;
        this.navPathList.add(FilenameUtils.getName(str) + "");
        this.fileList = AppConstants.getAllFile(this.rootPath);
        this.adapterNav.setList(this.navPathList);
        this.adapter.setList(this.fileList);
    }

    @Override
    public void onRefresh() {
        this.binding.swipeToRefresh.setRefreshing(false);
    }
}
