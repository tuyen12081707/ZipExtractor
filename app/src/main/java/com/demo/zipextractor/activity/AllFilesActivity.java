package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.zipextractor.adapter.AllFileAdapter;
import com.demo.zipextractor.adapter.NavPathAdapter;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;
import com.demo.zipextractor.utils.CheakBoxClickMain;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.utils.ZipManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityAllFilesBinding;
import com.demo.zipextractor.databinding.BottomsheetInfoBinding;
import com.demo.zipextractor.databinding.BottomsheetlayoutMultipleBinding;
import com.demo.zipextractor.databinding.DialogCompressBinding;
import com.demo.zipextractor.databinding.DialogDeleteBinding;
import com.demo.zipextractor.databinding.DialogPassuncomressBinding;
import com.demo.zipextractor.databinding.DialogRenameBinding;
import com.demo.zipextractor.databinding.DialogSortingBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class AllFilesActivity extends BaseActivity implements View.OnClickListener, AllFileAdapter.FragmentCommunication, RecyclerItemClick, CheakBoxClickMain {
    String ROOT_PATH;
    Activity activity;
    AllFileAdapter adapter;
    NavPathAdapter adapterNav;
    ActivityAllFilesBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    String compressionLevel;
    Context context;
    Dialog dialogCompress;
    DialogCompressBinding dialogCompressBinding;
    Dialog dialogDelete;
    DialogDeleteBinding dialogDeleteBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    Dialog dialogPassUncompress;
    DialogPassuncomressBinding dialogPassuncomressBinding;
    Dialog dialogRename;
    DialogRenameBinding dialogRenameBinding;
    DialogSortingBinding dialogSortBinding;
    BottomSheetDialog dialogsort;
    CompositeDisposable disposable;
    ZipFile extractFile;
    String format;
    MenuItem item;
    String nameFile;
    ArrayList<String> navPathList;
    String password;
    ProgressDialog progressDialog;
    File rootFile;
    SearchView searchView;
    String unzipLocation;
    int fileNo = 1;
    int itemPos = 0;
    int type = 4;
    List<File> fileArrayList = new ArrayList();
    StringBuilder navPath = new StringBuilder();
    ArrayList<File> mainList = new ArrayList<>();
    ArrayList<File> selectedFileList = new ArrayList<>();
    boolean isRename = false;
    boolean isOptionVisible = false;

    @Override
    public void setBinding() {
        this.binding = (ActivityAllFilesBinding) DataBindingUtil.setContentView(this, R.layout.activity_all_files);


        this.activity = this;
        this.context = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        setSupportActionBar(this.binding.toolBarAllfile.toolBar);

    }

    @Override
    public void setToolBar() {
        this.binding.toolBarAllfile.title.setText(R.string.internal_storage);
        this.binding.toolBarAllfile.cardBack.setOnClickListener(this);
        this.binding.toolBarAllfile.cardSort.setOnClickListener(this);
        this.binding.toolBarAllfile.cardCopy.setOnClickListener(this);
        this.binding.llExtract.setOnClickListener(this);
    }

    @Override
    public void initMethod() {
        initView();
        initClick();
        fillData();
        setAdapternavPath();
    }

    private void fillData() {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
            @Override
            public final Object call() {
                return AllFilesActivity.this.m52x4b3e35c5();
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
                AllFilesActivity.this.setAdapter();
                AllFilesActivity.this.binding.toolBarAllfile.cardSort.setVisibility(View.VISIBLE);
                AllFilesActivity.this.binding.rlProgess.setVisibility(View.GONE);
            }
        }));
    }


    public Boolean m52x4b3e35c5() {
        this.rootFile = new File(this.ROOT_PATH);
        this.mainList = AppConstants.getAllFile(this.ROOT_PATH);
        sortData();
        cheakNoData();
        return true;
    }

    private void initView() {
        this.ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + InternalZipConstants.ZIP_FILE_SEPARATOR;
        this.navPathList = new ArrayList<>();
        this.progressDialog = new ProgressDialog(this);
        this.disposable = new CompositeDisposable();
        this.dialogCompressBinding = (DialogCompressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_compress, null, false);
        this.dialogCompress = new Dialog(this);
        this.dialogPassuncomressBinding = (DialogPassuncomressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_passuncomress, null, false);
        this.dialogPassUncompress = new Dialog(this);
        this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheetlayout_multiple, null, false);
        this.dialogMultiBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.dialogSortBinding = (DialogSortingBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_sorting, null, false);
        this.dialogsort = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.dialogRenameBinding = (DialogRenameBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_rename, null, false);
        this.dialogRename = new Dialog(this, R.style.dialogTheme);
        this.dialogDeleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        this.dialogDelete = new Dialog(this);
        this.navPath.append("Internal Storage");
        this.binding.toolBarAllfile.title.setText(this.navPath);
        this.navPathList.add(0, "Internal Storage");
    }

    private void initClick() {
        this.dialogCompressBinding.buttonnCompress.setOnClickListener(this);
        this.dialogCompressBinding.buttonnCancel.setOnClickListener(this);
        this.dialogCompressBinding.checkBox.setOnClickListener(this);
        this.dialogCompressBinding.ll.setOnClickListener(this);
        this.dialogPassuncomressBinding.buttonnShowCancel.setOnClickListener(this);
        this.dialogPassuncomressBinding.buttonnShowCompress.setOnClickListener(this);
        this.dialogSortBinding.llNameAsc.setOnClickListener(this);
        this.dialogSortBinding.llNameDesc.setOnClickListener(this);
        this.dialogSortBinding.llDateAsc.setOnClickListener(this);
        this.dialogSortBinding.llDateDesc.setOnClickListener(this);
        this.dialogSortBinding.llSizeAsc.setOnClickListener(this);
        this.dialogSortBinding.llSizeDesc.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutCopy.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMove.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiDelete.setOnClickListener(this);
        this.binding.llMore.setOnClickListener(this);
        this.binding.llCompress.setOnClickListener(this);
        this.dialogRenameBinding.btncancel.setOnClickListener(this);
        this.dialogRenameBinding.btnOk.setOnClickListener(this);
        this.binding.fab.setOnClickListener(this);
        this.dialogDeleteBinding.btnDelCancel.setOnClickListener(this);
        this.dialogDeleteBinding.btnDelete.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    private void renameDialog() {
        this.dialogRename.setContentView(this.dialogRenameBinding.getRoot());
        Window window = this.dialogRename.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            this.dialogRename.getWindow().setGravity(17);
            this.dialogRename.getWindow().setBackgroundDrawableResource(17170445);
            this.dialogRename.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        this.dialogRename.show();
        if (this.isRename) {
            if (this.selectedFileList.size() > 0) {
                this.dialogRenameBinding.edRenameFileName.setText(FilenameUtils.getBaseName(this.selectedFileList.get(0).getPath()));
                return;
            }
            return;
        }
        this.dialogRenameBinding.txtAction.setText("Create");
        this.dialogRenameBinding.textView3.setText("Create Folder");
        this.dialogRenameBinding.textView3.setText("Create Folder");
    }


    public void setAdapter() {
        this.adapter = new AllFileAdapter(this, this.mainList, this.selectedFileList, this, this, false);
        this.binding.rvAllFile.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvAllFile.setItemViewCacheSize(this.mainList.size());
        this.binding.rvAllFile.setAdapter(this.adapter);
        this.binding.rvAllFile.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.item_animation));
        this.binding.rvAllFile.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (AllFilesActivity.this.isOptionVisible) {
                    AllFilesActivity.this.binding.fab.hide();
                    return;
                }
                if (i2 > 10 && AllFilesActivity.this.binding.fab.isShown()) {
                    AllFilesActivity.this.binding.fab.hide();
                }
                if (i2 < -10 && !AllFilesActivity.this.binding.fab.isShown()) {
                    AllFilesActivity.this.binding.fab.show();
                }
                if (recyclerView.canScrollVertically(-1)) {
                    return;
                }
                AllFilesActivity.this.binding.fab.show();
            }
        });
    }

    private void showConfirmationDeleteDialog() {
        this.dialogDelete.setContentView(this.dialogDeleteBinding.getRoot());
        this.dialogDelete.getWindow().setLayout(-1, -2);
        this.dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialogDelete.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        this.dialogDelete.show();
    }

    private void openSortingDialog() {
        this.dialogsort.setContentView(this.dialogSortBinding.getRoot());
        this.dialogsort.show();
        checkSelection();
    }

    private void ShowMultiBottomSheetDialog() {
        Iterator<File> it = this.selectedFileList.iterator();
        while (it.hasNext()) {
            if (it.next().isDirectory()) {
                this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setVisibility(View.GONE);
            } else {
                this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setVisibility(View.VISIBLE);
            }
        }
        this.dialogMultiBottomSheet.setContentView(this.bottomsheetlayoutMultipleBinding.getRoot());
        this.dialogMultiBottomSheet.show();
        if (this.selectedFileList.size() > 1) {
            this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.tvSortTitle.setVisibility(View.GONE);
            return;
        }
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.tvSortTitle.setText(this.selectedFileList.get(0).getName());
    }

    public String getFolderSizeLabel(long j) {
        if (j < 0 || j >= FileUtils.ONE_KB) {
            if (j < FileUtils.ONE_KB || j >= FileUtils.ONE_MB) {
                if (j < FileUtils.ONE_MB || j >= FileUtils.ONE_GB) {
                    if (j < FileUtils.ONE_GB || j >= 1099511627776L) {
                        if (j >= 1099511627776L) {
                            return (j / 1099511627776L) + " TB";
                        }
                        return j + " Bytes";
                    }
                    return (j / FileUtils.ONE_GB) + " GB";
                }
                return (j / FileUtils.ONE_MB) + " MB";
            }
            return (j / FileUtils.ONE_KB) + " KB";
        }
        return j + " B";
    }

    private void shareFile(File file) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intent.putExtra("android.intent.extra.STREAM", Uri.parse("content://" + file.getAbsolutePath()));
        startActivity(Intent.createChooser(intent, "Share File"));
    }

    private void openUnzipDialog() {
        this.dialogPassUncompress.setContentView(this.dialogPassuncomressBinding.getRoot());
        Window window = this.dialogPassUncompress.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
        }
        this.dialogPassUncompress.setCanceledOnTouchOutside(true);
        this.dialogPassUncompress.show();
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
        @SuppressLint("ResourceType") ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, 17367048, AppConstants.compressLevelList);
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
        this.nameFile = FilenameUtils.getBaseName(this.selectedFileList.get(0).getName());
        if (this.selectedFileList.size() > 1) {
            this.dialogCompressBinding.edFileName.setText("Internal Storage");
        } else {
            this.dialogCompressBinding.edFileName.setText(this.nameFile);
        }
        this.dialogCompressBinding.edSaveLocation.setText(this.ROOT_PATH);
    }

    public void showProgressDialog(String str) {
        this.progressDialog.setTitle(str);
        this.progressDialog.setMessage("Please wait......");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
        this.progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 4 || keyEvent.isCanceled()) {
                    return false;
                }
                AllFilesActivity.this.progressDialog.isShowing();
                return true;
            }
        });
    }

    private void zipFileFolders() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            this.fileArrayList.add(new File(this.selectedFileList.get(i).getAbsolutePath()));
        }
        if (this.password.isEmpty()) {
            Log.e("pass", "zipFileFolders: " + this.ROOT_PATH);
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FAST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FAST, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.FASTEST_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.FASTEST, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.MAXIMUM_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.MAXIMUM, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.ULTRA_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.ULTRA, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else if (this.compressionLevel.equalsIgnoreCase(AppConstants.STORE_LEVEL)) {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.HIGHER, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        } else {
            ZipManager.zipFileAndFolder(this.fileArrayList, CompressionLevel.NORMAL, this.ROOT_PATH + this.nameFile + this.format, this.password, false);
        }
        this.progressDialog.dismiss();
    }

    private void resetpassword() {
        this.dialogCompressBinding.checkBox.setChecked(false);
        this.dialogCompressBinding.layoutSetPassword.setVisibility(View.GONE);
        this.dialogCompressBinding.edPassword.setText("");
    }

    @Override
    public void respond(String str) {
        this.adapter.isSelectAll = false;
        this.ROOT_PATH = str + InternalZipConstants.ZIP_FILE_SEPARATOR;
        this.navPathList.add(FilenameUtils.getName(str) + "");
        this.adapterNav.setList(this.navPathList);
        this.mainList = AppConstants.getAllFile(this.ROOT_PATH);
        sortData();
        this.adapter.setList(this.mainList);
        cheakNoData();
    }

    private void setAdapternavPath() {
        this.adapterNav = new NavPathAdapter(this, this.navPathList, this);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.recyclerView.setAdapter(this.adapterNav);
    }

    private void cheakNoData() {
        if (this.mainList.size() == 0) {
            this.binding.tvNodata.setVisibility(View.VISIBLE);
        } else {
            this.binding.tvNodata.setVisibility(View.GONE);
        }
    }

    private void setImages(ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4, ImageView imageView5, ImageView imageView6) {
        imageView.setImageResource(R.drawable.radio_on);
        imageView2.setImageResource(R.drawable.radio_off);
        imageView3.setImageResource(R.drawable.radio_off);
        imageView4.setImageResource(R.drawable.radio_off);
        imageView5.setImageResource(R.drawable.radio_off);
        imageView6.setImageResource(R.drawable.radio_off);
    }

    @Override
    public void onRecyclerClick(int i) {
        this.itemPos = i;
        if (i > 0) {
            if (i <= this.navPathList.size()) {
                if (i != this.navPathList.size() - 1) {
                    ArrayList<String> arrayList = this.navPathList;
                    arrayList.subList(i + 1, arrayList.size()).clear();
                    this.ROOT_PATH = AppConstants.ROOT_PATH + "/0/";
                    for (int i2 = 1; i2 < this.navPathList.size(); i2++) {
                        this.ROOT_PATH += this.navPathList.get(i2) + InternalZipConstants.ZIP_FILE_SEPARATOR;
                    }
                }
            }
        } else if (i == 0) {
            this.ROOT_PATH = AppConstants.ROOT_PATH + "/0/";
            this.navPathList.clear();
            this.navPathList.add(0, "Internal Storage");
            this.type = 4;
        }
        this.mainList.clear();
        this.mainList = AppConstants.getAllFile(this.ROOT_PATH);
        sortData();
        this.adapter.setList(this.mainList);
        this.adapterNav.setList(this.navPathList);
        cheakNoData();
    }

    @Override
    public void onClick(View view) {
        int indexOf;
        int id = view.getId();
        String str = "";
        switch (id) {
            case R.id.btnDelCancel:
                this.dialogDelete.dismiss();
                return;
            case R.id.btnDelete:
                break;
            case R.id.btnOk:
                if (this.isRename) {
                    if (this.dialogRenameBinding.edRenameFileName.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "File Name Can not be Empty ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int indexOf2 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                    if (indexOf2 != -1) {
                        this.ROOT_PATH = this.mainList.get(indexOf2).getPath();
                    } else {
                        this.ROOT_PATH = this.mainList.get(this.itemPos).getPath();
                    }
                    File file = new File(this.ROOT_PATH, "");
                    String str2 = this.ROOT_PATH;
                    String substring = str2.substring(0, str2.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR));
                    StringBuilder append = new StringBuilder().append(this.dialogRenameBinding.edRenameFileName.getText().toString());
                    if (!TextUtils.isEmpty(FilenameUtils.getExtension(this.ROOT_PATH))) {
                        str = ".";
                    }
                    File file2 = new File(substring, append.append(str).append(FilenameUtils.getExtension(this.ROOT_PATH)).toString());
                    StringBuilder sb = new StringBuilder();
                    String str3 = this.ROOT_PATH;
                    if (AppConstants.cheakExits(sb.append(str3.substring(0, str3.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR))).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString(), this.dialogRenameBinding.edRenameFileName.getText().toString())) {
                        Toast.makeText(this.activity, "A Folder with this name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    file.renameTo(file2);
                    AppConstants.refreshGallery(file.getAbsolutePath(), this);
                    AppConstants.refreshGallery(file2.getAbsolutePath(), this);
                    int indexOf3 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                    if (indexOf3 != -1) {
                        this.mainList.set(indexOf3, file2);
                    }
                    if (this.itemPos != -1) {
                        this.adapter.getList().set(this.itemPos, file2);
                    }
                    refreshList();
                    this.dialogRename.dismiss();
                    return;
                } else if (this.dialogRenameBinding.edRenameFileName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Folder Name Can not be Empty ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    String str4 = this.ROOT_PATH;
                    if (AppConstants.cheakExits(sb2.append(str4.substring(0, str4.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR))).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString(), this.dialogRenameBinding.edRenameFileName.getText().toString())) {
                        Toast.makeText(this.activity, "A Folder with this name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File file3 = new File(this.ROOT_PATH, this.dialogRenameBinding.edRenameFileName.getText().toString());
                    if (!file3.exists()) {
                        file3.mkdirs();
                    }
                    this.mainList = AppConstants.getAllFile(this.ROOT_PATH);
                    sortData();
                    this.adapter.setList(this.mainList);
                    this.dialogRename.dismiss();
                    AppConstants.refreshGallery(file3.getAbsolutePath(), this);
                    this.dialogRenameBinding.edRenameFileName.setText("");
                    return;
                }
            case R.id.btncancel:
                this.dialogRename.dismiss();
                return;
            case R.id.buttonnCancel:
                resetpassword();
                this.dialogCompress.dismiss();
                return;
            case R.id.buttonnCompress:
                this.nameFile = this.dialogCompressBinding.edFileName.getText().toString();
                this.format = this.dialogCompressBinding.spinnerFormat.getSelectedItem().toString();
                this.compressionLevel = this.dialogCompressBinding.spinnerLevel.getSelectedItem().toString();
                this.password = this.dialogCompressBinding.edPassword.getText().toString();
                if (this.nameFile.isEmpty()) {
                    Toast.makeText(this, "Archive Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.binding.rlProgess.setVisibility(View.VISIBLE);
                getWindow().setFlags(16, 16);
                this.dialogCompress.dismiss();
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return AllFilesActivity.this.m59x637326cc();
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
                        AllFilesActivity.this.getWindow().clearFlags(16);
                        AllFilesActivity.this.binding.rlProgess.setVisibility(View.GONE);
                        Toast.makeText(AllFilesActivity.this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
                        AllFilesActivity.this.refreshList();
                    }
                }));
                return;
            case R.id.buttonnShowCancel:
                this.dialogPassUncompress.dismiss();
                return;
            case R.id.buttonnShowCompress:
                this.nameFile = this.selectedFileList.get(0).getName();
                final String path = this.selectedFileList.get(0).getPath();
                if (this.dialogPassuncomressBinding.edShowPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this.context, "Password can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    this.activityLauncher.launch(new Intent(this, SaveLocationActivity.class), new BetterActivityResult.OnActivityResult() {
                        @Override

                        public final void onActivityResult(Object obj) {
                            AllFilesActivity.this.m58xd638754b(path, (ActivityResult) obj);
                        }
                    });
                    return;
                }
            case R.id.cardBack:
                onBackPressed();
                return;
            case R.id.cardCopy:
                if (this.adapter.isSelectAll) {
                    this.selectedFileList.clear();
                    this.binding.toolBarAllfile.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_none));
                    this.adapter.unselectall();
                } else {
                    this.selectedFileList.clear();
                    this.selectedFileList.addAll(this.mainList);
                    this.binding.toolBarAllfile.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                    this.adapter.selectAll();
                }
                isAllSelected(this.selectedFileList);
                this.binding.tvItem.setText(this.selectedFileList.size() + " Items");
                this.binding.llExtract.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_inactive_color)));
                }
                this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_inactive_color));
                return;
            case R.id.cardSort:
                openSortingDialog();
                return;
            case R.id.checkBox:
                if (this.dialogCompressBinding.checkBox.isChecked()) {
                    this.dialogCompressBinding.layoutSetPassword.setVisibility(View.VISIBLE);
                    return;
                } else {
                    this.dialogCompressBinding.layoutSetPassword.setVisibility(View.GONE);
                    return;
                }
            case R.id.fab:
                renameDialog();
                return;
            case R.id.layoutCopy:
                Intent intent = new Intent(this, SaveLocationActivity.class);
                intent.putExtra("comeFrom", "Copy");
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AllFilesActivity.this.m62xb233b4f((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutInfo:
                long j = 0;
                for (int i = 0; i < this.selectedFileList.size(); i++) {
                    File file4 = new File(this.selectedFileList.get(i).getAbsolutePath());
                    if (file4.isDirectory()) {
                        for (File file5 : file4.listFiles()) {
                            j += file5.length();
                        }
                    } else {
                        j += file4.length();
                    }
                }
                String str5 = "" + getFolderSizeLabel(j);
                BottomsheetInfoBinding bottomsheetInfoBinding = (BottomsheetInfoBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_info, null, false);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(bottomsheetInfoBinding.getRoot());
                bottomSheetDialog.show();
                if (this.adapter.isFilter) {
                    int indexOf4 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                    bottomsheetInfoBinding.txtFilePath.setText(this.mainList.get(indexOf4).getAbsolutePath());
                    bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.mainList.get(indexOf4).lastModified())));
                } else {
                    bottomsheetInfoBinding.txtFilePath.setText(this.mainList.get(this.itemPos).getAbsolutePath());
                    bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.mainList.get(this.itemPos).lastModified())));
                }
                bottomsheetInfoBinding.txtFileSize.setText(str5);
                bottomsheetInfoBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        bottomSheetDialog.dismiss();
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMove:
                Intent intent2 = new Intent(this, SaveLocationActivity.class);
                intent2.putExtra("comeFrom", "Move");
                this.activityLauncher.launch(intent2, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AllFilesActivity.this.m54x47f14fe6((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMultiDelete:
                showConfirmationDeleteDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMultiShare:
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < this.selectedFileList.size(); i2++) {
                    arrayList.add(this.selectedFileList.get(i2).getAbsolutePath());
                    File file6 = new File((String) arrayList.get(i2));
                    if (file6.isDirectory()) {
                        for (File file7 : file6.listFiles()) {
                            arrayList.add(file7.getPath());
                        }
                    }
                }
                AppConstants.share(this, arrayList);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutrenam:
                this.isRename = true;
                renameDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.ll:
                Intent intent3 = new Intent(this.activity, SaveLocationActivity.class);
                intent3.putExtra("comeFrom", "Save");
                this.activityLauncher.launch(intent3, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AllFilesActivity.this.m60xf0add84d((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llCompress:
                Intent intent4 = new Intent(this, CompressActivity.class);
                intent4.putExtra("selectedFileListModel", this.selectedFileList);
                intent4.putExtra("isFile", true);
                this.activityLauncher.launch(intent4, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AllFilesActivity.this.m53x2e8860c8((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llDateAsc:
                this.type = 3;
                AppConstants.sortDateMainAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            case R.id.llDateDesc:
                this.type = 4;
                AppConstants.sortDateMainDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            case R.id.llExtract:
                try {
                    this.nameFile = this.adapter.getList().get(this.itemPos).getName();
                    if (new ZipFile(this.adapter.getList().get(this.itemPos).getPath()).isEncrypted()) {
                        openUnzipDialog();
                    } else {
                        this.activityLauncher.launch(new Intent(this, SaveLocationActivity.class), new BetterActivityResult.OnActivityResult() {
                            @Override

                            public final void onActivityResult(Object obj) {
                                AllFilesActivity.this.m55xd52c0167((ActivityResult) obj);
                            }
                        });
                    }
                    return;
                } catch (ZipException e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.llMore:
                ShowMultiBottomSheetDialog();
                return;
            case R.id.llNameAsc:
                this.type = 1;
                AppConstants.sortMainAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            case R.id.llNameDesc:
                this.type = 2;
                AppConstants.sortMainDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            case R.id.llSizeAsc:
                this.type = 5;
                AppConstants.sortSizeMainAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            case R.id.llSizeDesc:
                this.type = 6;
                AppConstants.sortSizeMainDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogsort.dismiss();
                return;
            default:
                return;
        }
        for (int i3 = 0; i3 < this.selectedFileList.size(); i3++) {
            File file8 = new File(this.selectedFileList.get(i3).getPath());
            try {
                int indexOf5 = this.adapter.getList().indexOf(this.selectedFileList.get(i3));
                ZipManager.doDelete(file8);
                AppConstants.refreshGallery(file8.getAbsolutePath(), this);
                if (indexOf5 != -1) {
                    this.adapter.getList().remove(indexOf5);
                }
                if (this.adapter.isFilter && (indexOf = this.mainList.indexOf(this.selectedFileList.get(i3))) != -1) {
                    this.mainList.remove(indexOf);
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        refreshList();
        this.dialogDelete.dismiss();
    }


    public void m53x2e8860c8(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            if (activityResult.getData() != null) {
                Toast.makeText(AllFilesActivity.this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
                AllFilesActivity.this.refreshList();
                AllFilesActivity allFilesActivity = AllFilesActivity.this;
                allFilesActivity.mainList = AppConstants.getAllFile(allFilesActivity.ROOT_PATH);
                AllFilesActivity.this.sortData();
                AllFilesActivity.this.adapter.setList(AllFilesActivity.this.mainList);
                return;
            }
            return;
        }
        refreshList();
    }


    public void m58xd638754b(final String str, ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
            @Override
            public final Object call() {
                return AllFilesActivity.this.m57x48fdc3ca(str);
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
                AllFilesActivity.this.progressDialog.dismiss();
            }
        }));
        this.binding.rlProgess.setVisibility(View.GONE);
        refreshList();
        this.dialogPassUncompress.dismiss();
        Toast.makeText(this, "Extract Successfully", Toast.LENGTH_SHORT).show();
    }


    public Boolean m57x48fdc3ca(String str) {
        try {
            try {
                String str2 = this.ROOT_PATH + this.nameFile;
                this.unzipLocation = this.ROOT_PATH + FilenameUtils.removeExtension(this.nameFile) + InternalZipConstants.ZIP_FILE_SEPARATOR;
                if (new File(this.unzipLocation).exists()) {
                    this.unzipLocation = this.ROOT_PATH + FilenameUtils.removeExtension(this.nameFile) + "_" + this.fileNo + InternalZipConstants.ZIP_FILE_SEPARATOR;
                    this.fileNo++;
                }
                ZipFile zipFile = new ZipFile(str, this.dialogPassuncomressBinding.edShowPassword.getText().toString().toCharArray());
                this.extractFile = zipFile;
                zipFile.extractAll(this.unzipLocation);
                this.extractFile.close();
                this.dialogPassUncompress.dismiss();
            } catch (ZipException e) {
                if (e.getType() == ZipException.Type.WRONG_PASSWORD) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public final void run() {
                            AllFilesActivity.this.m56xbbc31249();
                        }
                    });
                    e.printStackTrace();
                }
            }
            this.extractFile.close();
            return true;
        } catch (Throwable th) {
            try {
                this.extractFile.close();
                throw th;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public void m56xbbc31249() {
        this.progressDialog.dismiss();
        try {
            ZipManager.doDelete(new File(this.unzipLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
        this.dialogPassUncompress.show();
    }


    public Boolean m59x637326cc() {
        zipFileFolders();
        resetpassword();
        return true;
    }


    public void m60xf0add84d(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.dialogCompressBinding.edSaveLocation.setText(activityResult.getData().getStringExtra("path"));
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
    }


    public void m62xb233b4f(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        try {
            this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                @Override
                public final Object call() {
                    return AllFilesActivity.this.m61x7de889ce();
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
                    AllFilesActivity.this.binding.rlProgess.setVisibility(View.GONE);
                    AllFilesActivity.this.refreshList();
                    Toast.makeText(AllFilesActivity.this, "Copied Files Successfully", Toast.LENGTH_SHORT).show();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean m61x7de889ce() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            try {
                ZipManager.copy(this.selectedFileList.get(i).getAbsolutePath(), this.ROOT_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            AppConstants.refreshGallery(this.ROOT_PATH, this.context);
        }
        return true;
    }


    public void m54x47f14fe6(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disposable.add((Disposable) Observable.fromCallable(new Callable() {
                        @Override
                        public final Object call() {
                            return AllFilesActivity.this.m63x985decd0();
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
                        @Override
                        public void onError(Throwable th) {
                            Log.e("MYTAG", "ErrorNo: onError:" + th.getMessage());

                        }

                        @Override
                        public void onNext(Boolean bool) {
                            Log.e("MYTAG", "ErrorNo: onNext :" + bool);
                        }

                        @Override
                        public void onComplete() {
                            AllFilesActivity.this.binding.rlProgess.setVisibility(View.GONE);
                            AllFilesActivity.this.refreshList();
                            Toast.makeText(AllFilesActivity.this, "Moved Files Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }));

                }
            });

        } catch (Exception e) {
            Log.e("MYTAG", "ErrorNo:Moved Exception  :" + e);
            e.printStackTrace();
        }
    }


    public Boolean m63x985decd0() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            if (!new File(this.ROOT_PATH + InternalZipConstants.ZIP_FILE_SEPARATOR + this.selectedFileList.get(i).getName()).exists()) {
                try {
                    ZipManager.move(this.selectedFileList.get(i).getAbsolutePath(), this.ROOT_PATH);
                } catch (IOException e) {
                    Log.e("MYTAG", "ErrorNo: m63x985decd0:" + e);
                    throw new RuntimeException(e);
                }
                int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AllFilesActivity.this.adapter.removeItem(AllFilesActivity.this.selectedFileList);
                        AllFilesActivity.this.adapter.getList().removeAll(AllFilesActivity.this.selectedFileList);
                        AllFilesActivity.this.adapter.notifyItemChanged(finalI);
                    }
                });

            }
        }
        return true;
    }


    public void m55xd52c0167(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        try {
            if (new ZipFile(this.adapter.getList().get(this.itemPos).getPath()).isEncrypted()) {
                openUnzipDialog();
            } else {
                new ZipFile(this.adapter.getList().get(this.itemPos).getPath()).extractAll(FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.ROOT_PATH)));
            }
            this.binding.rlProgess.setVisibility(View.GONE);
            refreshList();
            Toast.makeText(this, "Extract Successfully", Toast.LENGTH_SHORT).show();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private void checkSelection() {
        int i = this.type;
        if (i == 1) {
            setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
        } else if (i == 2) {
            setImages(this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
        } else if (i == 3) {
            setImages(this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
        } else if (i == 4) {
            setImages(this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
        } else if (i == 5) {
            setImages(this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeDsc);
        } else if (i == 6) {
            setImages(this.dialogSortBinding.imgSizeDsc, this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc);
        }
    }


    public void refreshList() {
        this.binding.llOption.setVisibility(View.GONE);
        this.binding.toolBarAllfile.title.setText("Internal Storage");
        this.binding.toolBarAllfile.cardCopy.setVisibility(View.GONE);
        this.binding.toolBarAllfile.cardSort.setVisibility(View.VISIBLE);
        this.selectedFileList.clear();
        this.binding.fab.setVisibility(View.VISIBLE);
        this.adapter.notifyDataSetChanged();
        this.isOptionVisible = false;
    }

    @Override
    public void onBackPressed() {
        if (this.navPathList.size() > 1) {
            this.ROOT_PATH = AppConstants.ROOT_PATH + "/0/";
            this.binding.toolBarAllfile.title.setText("All Files");
            ArrayList<String> arrayList = this.navPathList;
            arrayList.subList(arrayList.size() - 1, this.navPathList.size()).clear();
            for (int i = 1; i < this.navPathList.size(); i++) {
                this.ROOT_PATH += this.navPathList.get(i) + InternalZipConstants.ZIP_FILE_SEPARATOR;
            }
            this.mainList = AppConstants.getAllFile(this.ROOT_PATH);
            this.adapterNav.setList(this.navPathList);
            sortData();
            this.adapter.setList(this.mainList);
            cheakNoData();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onCheakBoxClick(int i, List<File> list) {
        this.itemPos = i;
        if (this.selectedFileList.size() > 0) {
            this.binding.llOption.setVisibility(View.VISIBLE);
            this.isOptionVisible = true;
            this.binding.toolBarAllfile.cardCopy.setVisibility(View.VISIBLE);
            this.binding.toolBarAllfile.cardSort.setVisibility(View.GONE);
            this.binding.fab.setVisibility(View.GONE);
            this.binding.tvItem.setText(list.size() + " Items");
            if (this.selectedFileList.size() == 1) {
                if (this.selectedFileList.get(0).getName().endsWith(AppConstants.ZIP_FORMAT) || this.selectedFileList.get(0).getName().endsWith(AppConstants.TAR_FORMAT) || this.selectedFileList.get(0).getName().endsWith(AppConstants._7Z_FORMAT) || this.selectedFileList.get(0).getName().endsWith(AppConstants.RAR_FORMAT)) {
                    this.binding.llExtract.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_active_color)));
                    }
                    this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_active_color));
                } else {
                    this.binding.llExtract.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_inactive_color)));
                    }
                    this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_inactive_color));
                }
            } else {
                this.binding.llExtract.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_inactive_color)));
                }
                this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_inactive_color));
            }
        } else {
            this.binding.llOption.setVisibility(View.GONE);
            this.isOptionVisible = false;
            this.binding.fab.setVisibility(View.VISIBLE);
            this.binding.toolBarAllfile.cardCopy.setVisibility(View.GONE);
            this.binding.toolBarAllfile.cardSort.setVisibility(View.VISIBLE);
            this.binding.toolBarAllfile.title.setText("Internal Storage");
        }
        isAllSelected(list);
    }

    public void isAllSelected(List<File> list) {
        if (list.size() == this.mainList.size()) {
            this.binding.toolBarAllfile.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
        } else {
            this.binding.toolBarAllfile.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_none));
        }
        if (list.size() > 0) {
            this.binding.toolBarAllfile.title.setText("Select(" + list.size() + ")");
        } else {
            this.binding.toolBarAllfile.title.setText("Internal Storage");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.item = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        this.searchView = searchView;
        ((ImageView) searchView.findViewById(R.id.search_close_btn)).setImageResource(R.drawable.ic_close);
        EditText editText = (EditText) this.searchView.findViewById(R.id.search_src_text);
        editText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        editText.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
        this.searchView.setIconified(false);
        search(this.searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void search(SearchView searchView) {
        ((SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text)).setHint("Search");
        ((ImageView) searchView.findViewById(R.id.search_close_btn)).setImageResource(R.drawable.ic_close);
        EditText editText = (EditText) searchView.findViewById(R.id.search_src_text);
        editText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        editText.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                if (AllFilesActivity.this.adapter != null) {
                    AllFilesActivity.this.adapter.getFilter().filter(str, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int i) {
                            if (i == 0) {
                                AllFilesActivity.this.binding.tvNodata.setVisibility(View.VISIBLE);
                            } else {
                                AllFilesActivity.this.binding.tvNodata.setVisibility(View.GONE);
                            }
                        }
                    });
                    return false;
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                AllFilesActivity.this.sortData();
                AllFilesActivity.this.adapter.setList(AllFilesActivity.this.mainList);
                return false;
            }
        });
    }

    public void sortData() {
        int i = this.type;
        if (i == 1) {
            AppConstants.sortMainAsc(this.mainList);
        } else if (i == 2) {
            AppConstants.sortMainDesc(this.mainList);
        } else if (i == 3) {
            AppConstants.sortDateMainAsc(this.mainList);
        } else if (i == 4) {
            AppConstants.sortDateMainDesc(this.mainList);
        } else if (i == 5) {
            AppConstants.sortSizeMainAsc(this.mainList);
        } else if (i == 6) {
            AppConstants.sortSizeMainDesc(this.mainList);
        }
    }
}
