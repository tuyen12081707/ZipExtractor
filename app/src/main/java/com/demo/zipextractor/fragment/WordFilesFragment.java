package com.demo.zipextractor.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.zipextractor.adapter.AllDocsAdapter;
import com.demo.zipextractor.adapter.AllFileAdapter;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.utils.ZipManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.zipextractor.R;
import com.demo.zipextractor.activity.DocumentActivity;
import com.demo.zipextractor.activity.SaveLocationActivity;
import com.demo.zipextractor.databinding.BottomsheetInfoBinding;
import com.demo.zipextractor.databinding.BottomsheetlayoutMultipleBinding;
import com.demo.zipextractor.databinding.DialogCompressBinding;
import com.demo.zipextractor.databinding.DialogDeleteBinding;
import com.demo.zipextractor.databinding.DialogRenameBinding;
import com.demo.zipextractor.databinding.FragmentWordFilesBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class WordFilesFragment extends Fragment implements RecyclerItemClick, CheakBoxClick, View.OnClickListener {
    String ROOT_PATH;
    public AllDocsAdapter adapter;
    FragmentWordFilesBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    String compressionLevel;
    Dialog dialogCompress;
    DialogCompressBinding dialogCompressBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    Dialog dialogRename;
    DialogRenameBinding dialogRenameBinding;
    CompositeDisposable disposable;
    String format;
    String nameFile;
    String password;
    int itemPos = 0;
    List<File> fileArrayList = new ArrayList();

    @Override
    public void onCheakBoxClick(int i, ArrayList<FileListModel> arrayList) {
    }

    Activity activity = null;

    public WordFilesFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = (FragmentWordFilesBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_word_files, viewGroup, false);
        initView();
        initClick();
        setData();
        return this.binding.getRoot();
    }

    private void initView() {
        try {
         
            this.disposable = new CompositeDisposable();
            this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottomsheetlayout_multiple, null, false);
            this.dialogMultiBottomSheet = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
            this.dialogCompressBinding = (DialogCompressBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_compress, null, false);
            this.dialogCompress = new Dialog(getContext());
            this.dialogRenameBinding = (DialogRenameBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_rename, null, false);
            this.dialogRename = new Dialog(getContext(), R.style.dialogTheme);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MYTAG", "ErrorNo: initView:" +e);
        }
    }

    private void initClick() {
        this.bottomsheetlayoutMultipleBinding.layoutCopy.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMove.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiDelete.setOnClickListener(this);
        this.binding.llMore.setOnClickListener(this);
        this.binding.llCompress.setOnClickListener(this);
        this.dialogCompressBinding.buttonnCompress.setOnClickListener(this);
        this.dialogCompressBinding.buttonnCancel.setOnClickListener(this);
        this.dialogCompressBinding.checkBox.setOnClickListener(this);
        this.dialogCompressBinding.ll.setOnClickListener(this);
        this.dialogRenameBinding.btncancel.setOnClickListener(this);
        this.dialogRenameBinding.btnOk.setOnClickListener(this);
    }

    private void setData() {
        setAdapter();
    }

    public void setAdapter() {












        try {

            Log.e("MYTAG", "ErrorNo: activity():" + activity);
            Log.e("MYTAG", "ErrorNo: listAll:" + ((DocumentActivity) activity).listAll);
            Log.e("MYTAG", "ErrorNo: setAdapter:" + ((DocumentActivity) activity).selectedList);
            this.adapter = new AllDocsAdapter(activity, ((DocumentActivity) activity).listWord, ((DocumentActivity) activity).selectedList, this, (CheakBoxClick) getActivity());
            this.binding.rvAllList.setLayoutManager(new LinearLayoutManager(getActivity()));
            this.binding.rvAllList.setAdapter(this.adapter);
            this.binding.rvAllList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.item_animation));
            setWordData();
            checkListSize();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MYTAG", "ErrorNo: setAdapter:" + e);
        }
    }

    public void checkNoData() {
        if (this.adapter.getList().size() > 0) {
            this.binding.rvAllList.setVisibility(View.VISIBLE);
            this.binding.llNoData.setVisibility(View.GONE);
            return;
        }
        this.binding.rvAllList.setVisibility(View.GONE);
        this.binding.llNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecyclerClick(int i) {
        ((DocumentActivity) getContext()).isClicked = true;
        AllFileAdapter.openFile(getContext(), this.adapter.getModelByPosition(i).getFilePath(), new File(this.adapter.getModelByPosition(i).getFilePath()));
    }

    private void ShowMultiBottomSheetDialog() {
        this.dialogMultiBottomSheet.setContentView(this.bottomsheetlayoutMultipleBinding.getRoot());
        this.dialogMultiBottomSheet.show();
        if (((DocumentActivity) getContext()).selectedList.size() > 1) {
            this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.tvSortTitle.setVisibility(View.GONE);
            return;
        }
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.tvSortTitle.setText(((DocumentActivity) getContext()).selectedList.get(0).getFilename());
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
        @SuppressLint("ResourceType") ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), 17367048, AppConstants.fileFormatList);
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
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(getContext(), 17367048, AppConstants.compressLevelList);
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
        if (((DocumentActivity) getContext()).selectedList.size() > 1) {
            this.dialogCompressBinding.edFileName.setText("Internal Storage");
        } else {
            this.nameFile = FilenameUtils.getBaseName(((DocumentActivity) getContext()).listWord.get(this.itemPos).getFilename());
            this.dialogCompressBinding.edFileName.setText(this.nameFile);
        }
        this.dialogCompressBinding.edSaveLocation.setText(this.ROOT_PATH);
    }

    @SuppressLint("ResourceType")
    private void showConfirmationDeleteDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.dialogTheme);
        dialog.setContentView(((DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_delete, null, false)).getRoot());
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            dialog.getWindow().setGravity(17);
            dialog.getWindow().setBackgroundDrawableResource(17170445);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.show();
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
        this.dialogRenameBinding.edRenameFileName.setText(FilenameUtils.getBaseName(((DocumentActivity) getContext()).listWord.get(this.itemPos).getFilePath()));
    }


    public void refreshList() {
        this.binding.llOption.setVisibility(View.GONE);
        ((DocumentActivity) getContext()).selectedList.clear();
        this.adapter.notifyDataSetChanged();
    }

    public void setWordData() {
        Log.e("MYTAG", "ErrorNo: setWordData:" +adapter);
        Log.e("MYTAG", "ErrorNo: setWordData:" +DocumentActivity.documentActivity.listWord);
        this.adapter.setList(DocumentActivity.documentActivity.listWord);
    }

    private void zipFileFolders() {
        for (int i = 0; i < ((DocumentActivity) getContext()).selectedList.size(); i++) {
            this.fileArrayList.add(new File(((DocumentActivity) getContext()).selectedList.get(i).getFilePath()));
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
    }

    private void resetpassword() {
        this.dialogCompressBinding.checkBox.setChecked(false);
        this.dialogCompressBinding.layoutSetPassword.setVisibility(View.GONE);
        this.dialogCompressBinding.edPassword.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                if (this.dialogRenameBinding.edRenameFileName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "File Name Can not be Empty ", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.ROOT_PATH = ((DocumentActivity) getContext()).listWord.get(this.itemPos).getFilePath();
                File file = new File(this.ROOT_PATH, "");
                String str = this.ROOT_PATH;
                File file2 = new File(str.substring(0, str.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR)), this.dialogRenameBinding.edRenameFileName.getText().toString() + "." + FilenameUtils.getExtension(file.getPath()));
                StringBuilder sb = new StringBuilder();
                String str2 = this.ROOT_PATH;
                if (AppConstants.cheakExits(sb.append(str2.substring(0, str2.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR))).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString(), this.dialogRenameBinding.edRenameFileName.getText().toString())) {
                    Toast.makeText(getContext(), "A Folder with this name already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                file.renameTo(file2);
                AppConstants.refreshGallery(file2.getAbsolutePath(), getContext());
                FileListModel fileListModel = new FileListModel(file2.getPath(), file2.getName(), file2.length(), file2.lastModified(), "", "");
                this.adapter.getList().set(this.itemPos, fileListModel);
                ((DocumentActivity) getContext()).listWord.set(this.itemPos, fileListModel);
                setWordData();
                refreshList();
                this.adapter.notifyItemChanged(this.itemPos);
                this.dialogRename.dismiss();
                return;
            case R.id.btncancel:
                this.dialogRename.dismiss();
                return;
            case R.id.buttonnCompress:
                this.nameFile = this.dialogCompressBinding.edFileName.getText().toString();
                this.format = this.dialogCompressBinding.spinnerFormat.getSelectedItem().toString();
                this.compressionLevel = this.dialogCompressBinding.spinnerLevel.getSelectedItem().toString();
                this.password = this.dialogCompressBinding.edPassword.getText().toString();
                if (this.nameFile.isEmpty()) {
                    Toast.makeText(getContext(), "Archive Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.dialogCompress.dismiss();
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return WordFilesFragment.this.m132x8b097b7c();
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
                        Toast.makeText(getContext(), "Zip File Successfully", Toast.LENGTH_SHORT).show();
                        WordFilesFragment.this.refreshList();
                    }
                }));
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
                new Intent(getContext(), SaveLocationActivity.class);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutInfo:
                BottomsheetInfoBinding bottomsheetInfoBinding = (BottomsheetInfoBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottomsheet_info, null, false);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(bottomsheetInfoBinding.getRoot());
                bottomSheetDialog.show();
                bottomsheetInfoBinding.txtFilePath.setText(((DocumentActivity) getContext()).listWord.get(this.itemPos).getFilePath());
                bottomsheetInfoBinding.txtFileSize.setText(AppConstants.convertStorage(((DocumentActivity) getContext()).listWord.get(this.itemPos).getFileSize()));
                bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(((DocumentActivity) getContext()).listWord.get(this.itemPos).getFileDate())));
                bottomsheetInfoBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        bottomSheetDialog.dismiss();
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMove:
                new Intent(getContext(), SaveLocationActivity.class);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMultiDelete:
                showConfirmationDeleteDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMultiShare:
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < ((DocumentActivity) getContext()).selectedList.size(); i++) {
                    arrayList.add(((DocumentActivity) getContext()).selectedList.get(i).getFilePath());
                    File file3 = new File((String) arrayList.get(i));
                    if (file3.isDirectory()) {
                        for (File file4 : file3.listFiles()) {
                            arrayList.add(file4.getPath());
                        }
                    }
                }
                AppConstants.share(getContext(), arrayList);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutrenam:
                renameDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.ll:
                new Intent(getContext(), SaveLocationActivity.class);
                return;
            case R.id.llCompress:
                openCompressDialog();
                return;
            case R.id.llMore:
                ShowMultiBottomSheetDialog();
                return;
            case R.id.txdelete:
                showConfirmationDeleteDialog();
                return;
            default:
                return;
        }
    }


    public Boolean m132x8b097b7c() {
        zipFileFolders();
        resetpassword();
        return true;
    }

    public void checkListSize() {
        try {
            this.binding.llNoData.setVisibility(this.adapter.getList().size() > 0 ? View.GONE : View.VISIBLE);
        }catch (Exception e){
            Log.e("MYTAG", "ErrorNo: checkListSize:" +e);
        }
    }
}
