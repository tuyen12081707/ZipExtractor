package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
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

import com.demo.zipextractor.adapter.CommonFileAdapter;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.DocumentFetcher;
import com.demo.zipextractor.utils.FileRoot;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.utils.ZipManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityAudioBinding;
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
import java.util.List;
import java.util.concurrent.Callable;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class AudioActivity extends BaseActivity implements RecyclerItemClick, CheakBoxClick, View.OnClickListener {
    CommonFileAdapter adapter;
    ActivityAudioBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    String compressionLevel;
    DialogDeleteBinding deleteBinding;
    Dialog deleteDialog;
    Dialog dialogCompress;
    DialogCompressBinding dialogCompressBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    Dialog dialogPassUncompress;
    DialogPassuncomressBinding dialogPassuncomressBinding;
    Dialog dialogRename;
    DialogRenameBinding dialogRenameBinding;
    BottomSheetDialog dialogSort;
    DialogSortingBinding dialogSortBinding;
    CompositeDisposable disposable;
    String format;
    MenuItem item;
    int itemPos;
    ArrayList<FileListModel> mainList;
    String nameFile;
    String password;
    String rootPath;
    SearchView searchView;
    ArrayList<FileListModel> selectedFileList;
    String ROOT_PATH = AppConstants.ROOT_PATH1;
    boolean isClicked = false;
    int type = 4;
    List<File> fileArrayList = new ArrayList();
    boolean isSearch = false;

    @Override
    public void setBinding() {
        this.binding = (ActivityAudioBinding) DataBindingUtil.setContentView(this, R.layout.activity_audio);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        setSupportActionBar(this.binding.toolBarAudio.toolBar);

    }

    @Override
    public void setToolBar() {
        this.binding.toolBarAudio.title.setText(R.string.audio_title);
        this.binding.toolBarAudio.cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void initMethod() {
        initView();
        initClick();
        fillData();
    }

    private void initView() {
        this.disposable = new CompositeDisposable();
        this.selectedFileList = new ArrayList<>();
        this.mainList = new ArrayList<>();
        this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheetlayout_multiple, null, false);
        this.dialogMultiBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.dialogRenameBinding = (DialogRenameBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_rename, null, false);
        this.dialogRename = new Dialog(this, R.style.dialogTheme);
        this.dialogCompressBinding = (DialogCompressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_compress, null, false);
        this.dialogCompress = new Dialog(this);
        this.dialogPassuncomressBinding = (DialogPassuncomressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_passuncomress, null, false);
        this.dialogPassUncompress = new Dialog(this);
        this.dialogSortBinding = (DialogSortingBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_sorting, null, false);
        this.dialogSort = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.deleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        this.deleteDialog = new Dialog(this, R.style.dialogTheme);
    }

    private void initClick() {
        this.binding.toolBarAudio.cardSort.setOnClickListener(this);
        this.binding.toolBarAudio.cardCopy.setOnClickListener(this);
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
        this.deleteBinding.btnDelete.setOnClickListener(this);
        this.deleteBinding.btnDelete.setOnClickListener(this);
    }

    private void fillData() {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        new DocumentFetcher(this, FileRoot.AUDIO, FileRoot.AUDIO, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                AudioActivity.this.mainList.addAll(list);
                AudioActivity.this.setAdapter();
                AudioActivity.this.cheakEmptyData();
                AppConstants.sortDateDesc(AudioActivity.this.mainList);
            }
        });
        this.binding.rlProgess.setVisibility(View.GONE);
        this.binding.toolBarAudio.cardSort.setVisibility(View.VISIBLE);
    }


    public void setAdapter() {
        this.adapter = new CommonFileAdapter(this, this.mainList, this.selectedFileList, this, this);
        this.binding.rvAllList.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvAllList.setAdapter(this.adapter);
        this.binding.rvAllList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.item_animation));
    }


    public void cheakEmptyData() {
        this.binding.rvNodata.setVisibility(this.adapter.getList().size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void setImages(ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4, ImageView imageView5, ImageView imageView6) {
        imageView.setImageResource(R.drawable.radio_on);
        imageView2.setImageResource(R.drawable.radio_off);
        imageView3.setImageResource(R.drawable.radio_off);
        imageView4.setImageResource(R.drawable.radio_off);
        imageView5.setImageResource(R.drawable.radio_off);
        imageView6.setImageResource(R.drawable.radio_off);
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
        if (this.isSearch) {
            int indexOf = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
            if (indexOf != -1) {
                this.dialogCompressBinding.edFileName.setText(this.mainList.get(indexOf).getFilename());
            }
        } else if (this.selectedFileList.size() > 1) {
            this.dialogCompressBinding.edFileName.setText("Internal Storage");
        } else {
            this.dialogCompressBinding.edFileName.setText(FilenameUtils.getBaseName(this.mainList.get(this.itemPos).getFilename()));
        }
        this.dialogCompressBinding.edSaveLocation.setText(AppConstants.ROOT_PATH1);
    }

    private void openSortingDialog() {
        this.dialogSort.setContentView(this.dialogSortBinding.getRoot());
        this.dialogSort.show();
        checkSelection();
    }

    @Override
    public void onRecyclerClick(int i) {
        this.isClicked = true;
        this.itemPos = i;
        AppConstants.openWith(this, Uri.fromFile(new File(this.adapter.getList().get(i).getFilePath())), "audio/*");
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
        if (this.isSearch) {
            int indexOf = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
            if (indexOf != -1) {
                this.dialogRenameBinding.edRenameFileName.setText(FilenameUtils.getBaseName(this.mainList.get(indexOf).getFilePath()));
                return;
            }
            return;
        }
        this.dialogRenameBinding.edRenameFileName.setText(FilenameUtils.getBaseName(this.mainList.get(this.itemPos).getFilePath()));
    }

    private void ShowMultiBottomSheetDialog() {
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
        this.bottomsheetlayoutMultipleBinding.tvSortTitle.setText(this.selectedFileList.get(0).getFilename());
    }


    public void refreshList() {
        this.binding.llOption.setVisibility(View.GONE);
        this.binding.toolBarAudio.cardCopy.setVisibility(View.GONE);
        this.binding.toolBarAudio.cardSort.setVisibility(View.VISIBLE);
        this.selectedFileList.clear();
        this.adapter.notifyDataSetChanged();
        isAllSelected(this.selectedFileList);
    }

    @Override
    public void onCheakBoxClick(int i, ArrayList<FileListModel> arrayList) {
        this.isClicked = true;
        this.itemPos = i;
        if (arrayList.size() > 0) {
            this.binding.llOption.setVisibility(View.VISIBLE);
            this.binding.llExtract.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_inactive_color)));
            }
            this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_inactive_color));
            this.binding.toolBarAudio.cardSort.setVisibility(View.GONE);
            this.binding.toolBarAudio.cardCopy.setVisibility(View.VISIBLE);
            this.binding.tvItem.setText(arrayList.size() + " items");
        } else {
            this.binding.llOption.setVisibility(View.GONE);
            this.binding.toolBarAudio.cardSort.setVisibility(View.VISIBLE);
            this.binding.toolBarAudio.cardCopy.setVisibility(View.GONE);
        }
        isAllSelected(arrayList);
    }

    @SuppressLint("ResourceType")
    private void showConfirmationDeleteDialog() {
        this.deleteDialog.setContentView(this.deleteBinding.getRoot());
        Window window = this.deleteDialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            this.deleteDialog.getWindow().setGravity(17);
            this.deleteDialog.getWindow().setBackgroundDrawableResource(17170445);
            this.deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        this.deleteDialog.show();
    }

    private void resetpassword() {
        this.dialogCompressBinding.checkBox.setChecked(false);
        this.dialogCompressBinding.layoutSetPassword.setVisibility(View.GONE);
        this.dialogCompressBinding.edPassword.setText("");
    }

    private void zipFileFolders() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            this.fileArrayList.add(new File(this.selectedFileList.get(i).getFilePath()));
        }
        if (this.password.isEmpty()) {
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
        this.binding.rlProgess.setVisibility(View.GONE);
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

    public void isAllSelected(ArrayList<FileListModel> arrayList) {
        if (arrayList.size() == this.mainList.size()) {
            this.binding.toolBarAudio.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
        } else {
            this.binding.toolBarAudio.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_none));
        }
        if (arrayList.size() > 0) {
            this.binding.toolBarAudio.title.setText("Select(" + arrayList.size() + ")");
        } else {
            this.binding.toolBarAudio.title.setText("Audio");
        }
    }

    @Override
    public void onClick(View view) {
        int indexOf;
        int i = 0;
        switch (view.getId()) {
            case R.id.btnDelCancel:
                this.deleteDialog.dismiss();
                return;
            case R.id.btnDelete:
                break;
            case R.id.btnOk:
                if (this.dialogRenameBinding.edRenameFileName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "File Name Can not be Empty ", Toast.LENGTH_SHORT).show();
                    return;
                }
                int indexOf2 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                if (indexOf2 != -1) {
                    this.rootPath = this.mainList.get(indexOf2).getFilePath();
                } else {
                    this.rootPath = this.mainList.get(this.itemPos).getFilePath();
                }
                File file = new File(this.rootPath, "");
                String str = this.rootPath;
                File file2 = new File(str.substring(0, str.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR)), this.dialogRenameBinding.edRenameFileName.getText().toString() + "." + FilenameUtils.getExtension(this.rootPath));
                StringBuilder sb = new StringBuilder();
                String str2 = this.ROOT_PATH;
                if (AppConstants.cheakExits(sb.append(str2.substring(0, str2.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR))).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString(), this.dialogRenameBinding.edRenameFileName.getText().toString())) {
                    Toast.makeText(this, "A File with this name already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                file.renameTo(file2);
                AppConstants.refreshGallery(file.getAbsolutePath(), this);
                AppConstants.refreshGallery(file2.getAbsolutePath(), this);
                FileListModel fileListModel = new FileListModel(file2.getPath(), file2.getName(), file2.length(), file2.lastModified(), "", "");
                int indexOf3 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                if (indexOf3 != -1) {
                    this.mainList.set(indexOf3, fileListModel);
                }
                if (this.itemPos != -1) {
                    this.adapter.getList().set(this.itemPos, fileListModel);
                }
                refreshList();
                this.dialogRename.dismiss();
                return;
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
                this.binding.rlProgess.setVisibility(View.VISIBLE);
                if (this.nameFile.isEmpty()) {
                    Toast.makeText(this, "Archive Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.dialogCompress.dismiss();
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return AudioActivity.this.m84x6300a21e();
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
                        if (!AudioActivity.this.nameFile.isEmpty()) {
                            AudioActivity.this.binding.rlProgess.setVisibility(View.GONE);
                        }
                        AudioActivity.this.refreshList();
                        Toast.makeText(AudioActivity.this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
                    }
                }));
                return;
            case R.id.cardBack:
                onBackPressed();
                return;
            case R.id.cardCopy:
                if (this.adapter.isSelectAll) {
                    this.selectedFileList.clear();
                    this.binding.toolBarAudio.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_none));
                    this.adapter.unselectall();
                } else {
                    this.selectedFileList.clear();
                    this.selectedFileList.addAll(this.mainList);
                    this.binding.toolBarAudio.ivSelectAll.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
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
            case R.id.layoutCopy:
                Intent intent = new Intent(this, SaveLocationActivity.class);
                intent.putExtra("comeFrom", "Copy");
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AudioActivity.this.m82x7fad55e0((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutDelete:
            case R.id.layoutMultiDelete:
                showConfirmationDeleteDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutExtract:
                this.activityLauncher.launch(new Intent(this, SaveLocationActivity.class), new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AudioActivity.this.m79xaab06383((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutInfo:
                BottomsheetInfoBinding bottomsheetInfoBinding = (BottomsheetInfoBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_info, null, false);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(bottomsheetInfoBinding.getRoot());
                bottomSheetDialog.show();
                if (this.isSearch) {
                    int indexOf4 = this.mainList.indexOf(this.adapter.getList().get(this.itemPos));
                    bottomsheetInfoBinding.txtFilePath.setText(this.mainList.get(indexOf4).getFilePath());
                    bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.mainList.get(indexOf4).getFileDate())));
                } else {
                    bottomsheetInfoBinding.txtFilePath.setText(this.mainList.get(this.itemPos).getFilePath());
                    bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.mainList.get(this.itemPos).getFileDate())));
                }
                bottomsheetInfoBinding.txtFileSize.setText(AppConstants.convertStorage(this.adapter.getList().get(this.itemPos).getFileSize()));
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
                        AudioActivity.this.m83x7156fbff((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMultiShare:
                ArrayList arrayList = new ArrayList();
                while (i < this.selectedFileList.size()) {
                    arrayList.add(this.selectedFileList.get(i).getFilePath());
                    i++;
                }
                AppConstants.share(this, arrayList);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutReaname:
                renameDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutShare:
                shareFile(new File(this.adapter.getList().get(this.itemPos).getFilePath()));
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutView:
                if (this.adapter.getList().get(this.itemPos).getFilePath().contains(AppConstants.ZIP_FORMAT) || this.adapter.getList().get(this.itemPos).getFilePath().contains(AppConstants.TAR_FORMAT) || this.adapter.getList().get(this.itemPos).getFilePath().contains(AppConstants._7Z_FORMAT) || this.adapter.getList().get(this.itemPos).getFilePath().contains(AppConstants.RAR_FORMAT)) {
                    Intent intent3 = new Intent(this, ShowZipContentActivity.class);
                    intent3.putExtra("modelFileList", this.adapter.getList().get(this.itemPos));
                    intent3.putExtra("isComeArchive", true);
                    startActivity(intent3);
                }
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutrenam:
                if (this.selectedFileList.size() == 1) {
                    renameDialog();
                    this.dialogMultiBottomSheet.dismiss();
                    return;
                }
                return;
            case R.id.ll:
                Intent intent4 = new Intent(this, SaveLocationActivity.class);
                intent4.putExtra("comeFrom", "Save");
                this.activityLauncher.launch(intent4, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AudioActivity.this.m85x54aa483d((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llCompress:
                Intent intent5 = new Intent(this, CompressActivity.class);
                intent5.putExtra("selectedFileListModel", this.selectedFileList);
                this.activityLauncher.launch(intent5, new BetterActivityResult.OnActivityResult() {
                    @Override

                    public final void onActivityResult(Object obj) {
                        AudioActivity.this.m80x9c5a09a2((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llDateAsc:
                this.type = 3;
                AppConstants.sortDateAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            case R.id.llDateDesc:
                this.type = 4;
                AppConstants.sortDateDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            case R.id.llMore:
                ShowMultiBottomSheetDialog();
                return;
            case R.id.llNameAsc:
                this.type = 1;
                AppConstants.sortAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            case R.id.llNameDesc:
                this.type = 2;
                AppConstants.sortDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            case R.id.llSizeAsc:
                this.type = 5;
                AppConstants.sortSizeAsc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            case R.id.llSizeDesc:
                this.type = 6;
                AppConstants.sortSizeDesc(this.mainList);
                setImages(this.dialogSortBinding.imgNameAsc, this.dialogSortBinding.imgNameDsc, this.dialogSortBinding.imgDateAsc, this.dialogSortBinding.imgDateDsc, this.dialogSortBinding.imgSizeAsc, this.dialogSortBinding.imgSizeDsc);
                this.adapter.setList(this.mainList);
                this.dialogSort.dismiss();
                return;
            default:
                return;
        }
        while (i < this.selectedFileList.size()) {
            File file3 = new File(this.selectedFileList.get(i).getFilePath());
            try {
                int indexOf5 = this.adapter.getList().indexOf(this.selectedFileList.get(i));
                ZipManager.doDelete(file3);
                AppConstants.refreshGallery(file3.getAbsolutePath(), this);
                if (indexOf5 != -1) {
                    this.adapter.getList().remove(indexOf5);
                }
                if (this.adapter.isFilter && (indexOf = this.mainList.indexOf(this.selectedFileList.get(i))) != -1) {
                    this.mainList.remove(indexOf);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
        refreshList();
        cheakEmptyData();
        this.deleteDialog.dismiss();
    }


    public void m79xaab06383(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.rootPath = activityResult.getData().getStringExtra("path") + InternalZipConstants.ZIP_FILE_SEPARATOR;
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        try {
            new ZipFile(this.adapter.getList().get(this.itemPos).getFilePath()).extractAll(FilenameUtils.removeExtension(FilenameUtils.removeExtension(this.rootPath)));
            this.binding.rlProgess.setVisibility(View.GONE);
            refreshList();
            Toast.makeText(this, "Extract Successfully", Toast.LENGTH_SHORT).show();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


    public void m80x9c5a09a2(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Toast.makeText(this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
        refreshList();
    }


    public void m82x7fad55e0(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.rootPath = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        try {
            this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                @Override
                public final Object call() {
                    return AudioActivity.this.m81x8e03afc1();
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
                    AudioActivity.this.binding.rlProgess.setVisibility(View.GONE);
                    AudioActivity.this.refreshList();
                    Toast.makeText(AudioActivity.this, "Copied Files Successfully", Toast.LENGTH_SHORT).show();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean m81x8e03afc1() {
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            try {
                ZipManager.copy(this.selectedFileList.get(i).getFilePath(), this.rootPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.e("Copy", "onClick: " + this.selectedFileList.get(i).getFilePath());
        }
        return true;
    }


    public void m83x7156fbff(ActivityResult activityResult) {
        int indexOf;
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.rootPath = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        for (int i = 0; i < this.selectedFileList.size(); i++) {
            try {
                if (!new File(this.rootPath + InternalZipConstants.ZIP_FILE_SEPARATOR + this.selectedFileList.get(i).getFilename()).exists()) {
                    ZipManager.move(this.selectedFileList.get(i).getFilePath(), this.rootPath);
                    AppConstants.refreshGallery(this.rootPath + this.selectedFileList.get(i).getFilename(), this);
                    this.selectedFileList.get(i).setFilePath(this.rootPath + this.selectedFileList.get(i).getFilename());
                    int indexOf2 = this.adapter.getList().indexOf(this.selectedFileList.get(i));
                    if (indexOf2 != -1) {
                        this.adapter.getList().set(indexOf2, this.selectedFileList.get(i));
                    }
                    if (this.adapter.isFilter && (indexOf = this.mainList.indexOf(this.selectedFileList.get(i))) != -1) {
                        this.mainList.set(indexOf, this.selectedFileList.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        this.binding.rlProgess.setVisibility(View.GONE);
        refreshList();
        Toast.makeText(this, "Moved Files Successfully", Toast.LENGTH_SHORT).show();
    }


    public Boolean m84x6300a21e() {
        zipFileFolders();
        resetpassword();
        return true;
    }


    public void m85x54aa483d(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            if (activityResult.getData() != null) {
                this.dialogCompressBinding.edSaveLocation.setText(activityResult.getData().getStringExtra("path"));
                this.ROOT_PATH = activityResult.getData().getStringExtra("path");
                return;
            }
            this.ROOT_PATH = AppConstants.ROOT_PATH1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
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
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHint("Search");
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.white));
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
                if (AudioActivity.this.adapter != null) {
                    AudioActivity.this.adapter.getFilter().filter(str, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int i) {
                            if (i == 0) {
                                AudioActivity.this.binding.rvNodata.setVisibility(View.VISIBLE);
                            } else {
                                AudioActivity.this.binding.rvNodata.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                AudioActivity.this.isSearch = true;
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                AudioActivity.this.isSearch = false;
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.isClicked) {
            AudioActivity.this.finish();

        } else {
            finish();
        }
    }
}
