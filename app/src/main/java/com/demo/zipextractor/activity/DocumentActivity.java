package com.demo.zipextractor.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.demo.zipextractor.fragment.AllFileShowFragment;
import com.demo.zipextractor.fragment.ExcelFilesFragment;
import com.demo.zipextractor.fragment.PDFFileFragment;
import com.demo.zipextractor.fragment.PPTFileFragment;
import com.demo.zipextractor.fragment.TXTFileFragment;
import com.demo.zipextractor.fragment.WordFilesFragment;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.BaseActivity;
import com.demo.zipextractor.utils.BetterActivityResult;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.DocumentFetcher;
import com.demo.zipextractor.utils.FileRoot;
import com.demo.zipextractor.utils.MainConstant;
import com.demo.zipextractor.utils.ZipManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ActivityDocumentBinding;
import com.demo.zipextractor.databinding.BottomsheetInfoBinding;
import com.demo.zipextractor.databinding.BottomsheetlayoutMultipleBinding;
import com.demo.zipextractor.databinding.DialogCompressBinding;
import com.demo.zipextractor.databinding.DialogDeleteBinding;
import com.demo.zipextractor.databinding.DialogRenameBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class DocumentActivity extends BaseActivity implements CheakBoxClick, View.OnClickListener {
    String ROOT_PATH;
    Fragment activeFragment;
    AllFileShowFragment allFileShowFragment;
    ActivityDocumentBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    FileListModel clickModel;
    String compressionLevel;
    Dialog deleteDialog;
    Dialog dialogCompress;
    DialogCompressBinding dialogCompressBinding;
    DialogDeleteBinding dialogDeleteBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    Dialog dialogRename;
    DialogRenameBinding dialogRenameBinding;
    CompositeDisposable disposable;
    ExcelFilesFragment excelFilesFragment;
    String format;
    FragmentManager fragmentManager;
    MenuItem item;
    public ArrayList<FileListModel> listAll;
    public ArrayList<FileListModel> listExcel;
    public ArrayList<FileListModel> listPdf;
    public ArrayList<FileListModel> listPpt;
    public ArrayList<FileListModel> listTxt;
    public ArrayList<FileListModel> listWord;
    String nameFile;
    String password;
    PDFFileFragment pdfFileFragment;
    PPTFileFragment pptFileFragment;
    SearchView searchView;
    public ArrayList<FileListModel> selectedList;
    TXTFileFragment txtFileFragment;
    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    WordFilesFragment wordFilesFragment;
    String[] tabTitle = {"All", "Word", "Excel", MainConstant.FILE_TYPE_PDF_CAPS, "PPT", "TXT"};
    List<Fragment> listFragment = new ArrayList();
    public boolean isClicked = false;
    int index = -1;
    List<File> fileArrayList = new ArrayList();
    public static DocumentActivity documentActivity = null;

    @Override
    public void setBinding() {
        documentActivity = this;
        ActivityDocumentBinding activityDocumentBinding = (ActivityDocumentBinding) DataBindingUtil.setContentView(this, R.layout.activity_document);
        this.binding = activityDocumentBinding;



        setSupportActionBar(activityDocumentBinding.toolbarDocument.toolBar);
    }

    @Override
    public void setToolBar() {
        this.binding.toolbarDocument.title.setText("Documents");
        this.binding.toolbarDocument.cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void initMethod() {
        initFragment();
        initClick();
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
        this.dialogDeleteBinding.btnDelCancel.setOnClickListener(this);
        this.dialogDeleteBinding.btnDelete.setOnClickListener(this);
    }

    private void initFragment() {
        this.listAll = new ArrayList<>();
        this.listWord = new ArrayList<>();
        this.listExcel = new ArrayList<>();
        this.listPdf = new ArrayList<>();
        this.listPpt = new ArrayList<>();
        this.listTxt = new ArrayList<>();
        this.selectedList = new ArrayList<>();
        this.disposable = new CompositeDisposable();
        this.dialogDeleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        this.deleteDialog = new Dialog(this, R.style.dialogTheme);
        this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheetlayout_multiple, null, false);
        this.dialogMultiBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        this.dialogCompressBinding = (DialogCompressBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_compress, null, false);
        this.dialogCompress = new Dialog(this, R.style.dialogTheme);
        this.dialogRenameBinding = (DialogRenameBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_rename, null, false);
        this.dialogRename = new Dialog(this, R.style.dialogTheme);
        fillData();
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

    private void fillData() {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        new DocumentFetcher(this, FileRoot.ALL_DOC, FileRoot.ALL_DOC, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m89x9b61f60b(list);
            }
        });
        new DocumentFetcher(this, FileRoot.WORD, FileRoot.WORD, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m90x289ca78c(list);
            }
        });
        new DocumentFetcher(this, FileRoot.EXCEL, FileRoot.EXCEL, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m91xb5d7590d(list);
            }
        });
        new DocumentFetcher(this, FileRoot.PDF, FileRoot.PDF, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m92x43120a8e(list);
            }
        });
        new DocumentFetcher(this, FileRoot.PPT, FileRoot.PPT, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m93xd04cbc0f(list);
            }
        });
        new DocumentFetcher(this, FileRoot.TXT, FileRoot.TXT, "", DocumentFetcher.ORDER_AZ, new DocumentFetcher.OnFileFetchListnear() {
            @Override
            public final void onFileFetched(List list) {
                DocumentActivity.this.m94x5d876d90(list);
            }
        });
    }


    public void m89x9b61f60b(List list) {
        this.listAll.addAll(list);
        this.allFileShowFragment = new AllFileShowFragment(this);
        this.allFileShowFragment.setAdapter();
        this.wordFilesFragment = new WordFilesFragment(this);
        this.wordFilesFragment.setAdapter();

        this.excelFilesFragment = new ExcelFilesFragment(this);
        this.excelFilesFragment.setAdapter();
        this.pdfFileFragment = new PDFFileFragment(this);
        this.pdfFileFragment.setAdapter();
        this.pptFileFragment = new PPTFileFragment(this);
        this.pptFileFragment.setAdapter();
        this.txtFileFragment = new TXTFileFragment(this);
        this.txtFileFragment.setAdapter();
        this.fragmentManager = getSupportFragmentManager();
        this.activeFragment = this.allFileShowFragment;
        this.binding.viewPager.setOffscreenPageLimit(6);
        this.listFragment.add(this.allFileShowFragment);
        this.listFragment.add(this.wordFilesFragment);
        this.listFragment.add(this.excelFilesFragment);
        this.listFragment.add(this.pdfFileFragment);
        this.listFragment.add(this.pptFileFragment);
        this.listFragment.add(this.txtFileFragment);
        this.viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this.fragmentManager, getLifecycle(), this.listFragment);
        this.binding.viewPager.setAdapter(this.viewPagerFragmentAdapter);
        new TabLayoutMediator(this.binding.tabLayout, this.binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public final void onConfigureTab(TabLayout.Tab tab, int i) {
                DocumentActivity.this.m88xe27448a(tab, i);
            }
        }).attach();
        this.binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DocumentActivity.this.item.collapseActionView();
                DocumentActivity.this.setDocsData();
                int position = tab.getPosition();
                if (position == 0) {
                    DocumentActivity documentActivity = DocumentActivity.this;
                    documentActivity.activeFragment = documentActivity.allFileShowFragment;
                } else if (position == 1) {
                    DocumentActivity documentActivity2 = DocumentActivity.this;
                    documentActivity2.activeFragment = documentActivity2.wordFilesFragment;
                } else if (position == 2) {
                    DocumentActivity documentActivity3 = DocumentActivity.this;
                    documentActivity3.activeFragment = documentActivity3.excelFilesFragment;
                } else if (position == 3) {
                    DocumentActivity documentActivity4 = DocumentActivity.this;
                    documentActivity4.activeFragment = documentActivity4.pdfFileFragment;
                } else if (position == 4) {
                    DocumentActivity documentActivity5 = DocumentActivity.this;
                    documentActivity5.activeFragment = documentActivity5.pptFileFragment;
                } else if (position != 5) {
                } else {
                    DocumentActivity documentActivity6 = DocumentActivity.this;
                    documentActivity6.activeFragment = documentActivity6.txtFileFragment;
                }
            }
        });
    }


    public void m88xe27448a(TabLayout.Tab tab, int i) {
        tab.setText(this.tabTitle[i]);
    }


    public void m90x289ca78c(List list) {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.listWord.addAll(list);
        AppConstants.sortDateDesc(this.listWord);
        WordFilesFragment wordFilesFragment = this.wordFilesFragment;
        if (wordFilesFragment != null && wordFilesFragment.adapter != null) {
            this.wordFilesFragment.adapter.notifyDataSetChanged();
            this.wordFilesFragment.checkListSize();
        }
        this.binding.rlProgess.setVisibility(View.GONE);
    }


    public void m91xb5d7590d(List list) {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.listExcel.addAll(list);
        AppConstants.sortDateDesc(this.listExcel);
        ExcelFilesFragment excelFilesFragment = this.excelFilesFragment;
        if (excelFilesFragment != null && excelFilesFragment.adapter != null) {
            this.excelFilesFragment.adapter.notifyDataSetChanged();
            this.excelFilesFragment.checkListSize();
        }
        this.binding.rlProgess.setVisibility(View.GONE);
    }


    public void m92x43120a8e(List list) {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.listPdf.addAll(list);
        AppConstants.sortDateDesc(this.listPdf);
        PDFFileFragment pDFFileFragment = this.pdfFileFragment;
        if (pDFFileFragment != null && pDFFileFragment.adapter != null) {
            this.pdfFileFragment.adapter.notifyDataSetChanged();
            this.pdfFileFragment.checkListSize();
        }
        this.binding.rlProgess.setVisibility(View.GONE);
    }


    public void m93xd04cbc0f(List list) {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.listPpt.addAll(list);
        AppConstants.sortDateDesc(this.listPpt);
        PPTFileFragment pPTFileFragment = this.pptFileFragment;
        if (pPTFileFragment != null && pPTFileFragment.adapter != null) {
            this.pptFileFragment.adapter.notifyDataSetChanged();
            this.pptFileFragment.checkListSize();
        }
        this.binding.rlProgess.setVisibility(View.GONE);
    }


    public void m94x5d876d90(List list) {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.listTxt.addAll(list);
        AppConstants.sortDateDesc(this.listTxt);
        TXTFileFragment tXTFileFragment = this.txtFileFragment;
        if (tXTFileFragment != null && tXTFileFragment.adapter != null) {
            this.txtFileFragment.adapter.notifyDataSetChanged();
            this.txtFileFragment.checkListSize();
        }
        this.binding.rlProgess.setVisibility(View.GONE);
    }

    public void showFragment(Fragment fragment) {
        if (this.activeFragment.equals(fragment)) {
            return;
        }
        this.fragmentManager.beginTransaction().show(fragment).hide(this.activeFragment).commit();
        this.activeFragment = fragment;
    }

    @Override
    public void onCheakBoxClick(int i, ArrayList<FileListModel> arrayList) {
        this.isClicked = true;
        this.clickModel = arrayList.get(i);
        Log.d("TAG", "onCheakBoxClick: ");
        if (this.selectedList.size() > 0) {
            this.binding.llOption.setVisibility(View.VISIBLE);
            this.binding.tvItem.setText(this.selectedList.size() + " items");
        } else {
            this.binding.llOption.setVisibility(View.GONE);
        }
        int indexOf = this.listAll.indexOf(this.clickModel);
        this.index = indexOf;
        if (indexOf != -1) {
            this.allFileShowFragment.adapter.notifyItemChanged(this.index);
        }
        int indexOf2 = this.listExcel.indexOf(this.clickModel);
        this.index = indexOf2;
        if (indexOf2 != -1) {
            this.excelFilesFragment.adapter.notifyItemChanged(this.index);
        }
        int indexOf3 = this.listPpt.indexOf(this.clickModel);
        this.index = indexOf3;
        if (indexOf3 != -1) {
            this.pptFileFragment.adapter.notifyItemChanged(this.index);
        }
        int indexOf4 = this.listTxt.indexOf(this.clickModel);
        this.index = indexOf4;
        if (indexOf4 != -1) {
            this.txtFileFragment.adapter.notifyItemChanged(this.index);
        }
        int indexOf5 = this.listWord.indexOf(this.clickModel);
        this.index = indexOf5;
        if (indexOf5 != -1) {
            this.wordFilesFragment.adapter.notifyItemChanged(this.index);
        }
        int indexOf6 = this.listPdf.indexOf(this.clickModel);
        this.index = indexOf6;
        if (indexOf6 != -1) {
            this.pdfFileFragment.adapter.notifyItemChanged(this.index);
        }
        this.binding.llExtract.setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.binding.icExtract.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.extract_inactive_color)));
        }
        this.binding.tvExtract.setTextColor(ContextCompat.getColor(this, R.color.extract_inactive_color));
    }

    private void ShowMultiBottomSheetDialog() {
        this.dialogMultiBottomSheet.setContentView(this.bottomsheetlayoutMultipleBinding.getRoot());
        this.dialogMultiBottomSheet.show();
        if (this.selectedList.size() > 1) {
            this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.GONE);
            this.bottomsheetlayoutMultipleBinding.tvSortTitle.setVisibility(View.GONE);
            return;
        }
        this.bottomsheetlayoutMultipleBinding.layoutrenam.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setVisibility(View.VISIBLE);
        this.bottomsheetlayoutMultipleBinding.tvSortTitle.setText(this.selectedList.get(0).getFilename());
    }

    @SuppressLint("ResourceType")
    private void showConfirmationDeleteDialog() {
        this.deleteDialog.setContentView(this.dialogDeleteBinding.getRoot());
        Window window = this.deleteDialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            this.deleteDialog.getWindow().setGravity(17);
            this.deleteDialog.getWindow().setBackgroundDrawableResource(17170445);
            this.deleteDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        this.deleteDialog.show();
    }

    private void refreshList() {
        this.binding.llOption.setVisibility(View.GONE);
        this.binding.toolbarDocument.cardCopy.setVisibility(View.GONE);
        this.binding.toolbarDocument.cardSort.setVisibility(View.VISIBLE);
        this.selectedList.clear();
        this.allFileShowFragment.adapter.notifyDataSetChanged();
        this.excelFilesFragment.adapter.notifyDataSetChanged();
        this.pptFileFragment.adapter.notifyDataSetChanged();
        this.txtFileFragment.adapter.notifyDataSetChanged();
        this.wordFilesFragment.adapter.notifyDataSetChanged();
        this.pdfFileFragment.adapter.notifyDataSetChanged();
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
        this.dialogRenameBinding.edRenameFileName.setText(FilenameUtils.getBaseName(this.selectedList.get(0).getFilePath()));
    }

    private void zipFileFolders() {
        for (int i = 0; i < this.selectedList.size(); i++) {
            this.fileArrayList.add(new File(this.selectedList.get(i).getFilePath()));
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

    @Override
    public void onClick(View view) {
        int indexOf;
        int indexOf2;
        int indexOf3;
        int indexOf4;
        int indexOf5;
        int indexOf6;
        int indexOf7;
        int indexOf8;
        int indexOf9;
        int indexOf10;
        int indexOf11;
        int indexOf12;
        int indexOf13;
        int indexOf14;
        int indexOf15;
        int indexOf16;
        int indexOf17;
        int indexOf18;
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
                if (this.selectedList.size() > 0) {
                    this.ROOT_PATH = this.selectedList.get(0).getFilePath();
                }
                File file = new File(this.ROOT_PATH, "");
                String str = this.ROOT_PATH;
                File file2 = new File(str.substring(0, str.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR)), this.dialogRenameBinding.edRenameFileName.getText().toString() + "." + FilenameUtils.getExtension(file.getPath()));
                StringBuilder sb = new StringBuilder();
                String str2 = this.ROOT_PATH;
                if (AppConstants.cheakExits(sb.append(str2.substring(0, str2.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR))).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString(), this.dialogRenameBinding.edRenameFileName.getText().toString())) {
                    Toast.makeText(this, "A file name already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                file.renameTo(file2);
                AppConstants.refreshGallery(file.getPath(), this);
                AppConstants.refreshGallery(file2.getPath(), this);
                FileListModel fileListModel = new FileListModel(file.getPath(), file.getName(), file.length(), file.lastModified(), "", "");
                FileListModel fileListModel2 = new FileListModel(file2.getPath(), file2.getName(), file2.length(), file2.lastModified(), "", "");
                AllFileShowFragment allFileShowFragment = this.allFileShowFragment;
                if (allFileShowFragment != null && allFileShowFragment.adapter != null && (indexOf17 = this.allFileShowFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.allFileShowFragment.adapter.isFilter && (indexOf18 = this.listAll.indexOf(fileListModel)) != -1) {
                        this.listAll.set(indexOf18, fileListModel2);
                    }
                    this.allFileShowFragment.adapter.getList().set(indexOf17, fileListModel2);
                    this.allFileShowFragment.adapter.notifyItemChanged(indexOf17);
                }
                WordFilesFragment wordFilesFragment = this.wordFilesFragment;
                if (wordFilesFragment != null && wordFilesFragment.adapter != null && (indexOf15 = this.wordFilesFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.wordFilesFragment.adapter.isFilter && (indexOf16 = this.listWord.indexOf(fileListModel)) != -1) {
                        this.listWord.set(indexOf16, fileListModel2);
                    }
                    this.wordFilesFragment.adapter.getList().set(indexOf15, fileListModel2);
                    this.wordFilesFragment.adapter.notifyItemChanged(indexOf15);
                }
                ExcelFilesFragment excelFilesFragment = this.excelFilesFragment;
                if (excelFilesFragment != null && excelFilesFragment.adapter != null && (indexOf13 = this.excelFilesFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.excelFilesFragment.adapter.isFilter && (indexOf14 = this.listExcel.indexOf(fileListModel)) != -1) {
                        this.listExcel.set(indexOf14, fileListModel2);
                    }
                    this.excelFilesFragment.adapter.getList().set(indexOf13, fileListModel2);
                    this.excelFilesFragment.adapter.notifyItemChanged(indexOf13);
                }
                PDFFileFragment pDFFileFragment = this.pdfFileFragment;
                if (pDFFileFragment != null && pDFFileFragment.adapter != null && (indexOf11 = this.pdfFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.pdfFileFragment.adapter.isFilter && (indexOf12 = this.listPdf.indexOf(fileListModel)) != -1) {
                        this.listPdf.set(indexOf12, fileListModel2);
                    }
                    this.pdfFileFragment.adapter.getList().set(indexOf11, fileListModel2);
                    this.pdfFileFragment.adapter.notifyItemChanged(indexOf11);
                }
                PPTFileFragment pPTFileFragment = this.pptFileFragment;
                if (pPTFileFragment != null && pPTFileFragment.adapter != null && (indexOf9 = this.pptFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.pptFileFragment.adapter.isFilter && (indexOf10 = this.listPpt.indexOf(fileListModel)) != -1) {
                        this.listPpt.set(indexOf10, fileListModel2);
                    }
                    this.pptFileFragment.adapter.getList().set(indexOf9, fileListModel2);
                    this.pptFileFragment.adapter.notifyItemChanged(indexOf9);
                }
                TXTFileFragment tXTFileFragment = this.txtFileFragment;
                if (tXTFileFragment != null && tXTFileFragment.adapter != null && (indexOf7 = this.txtFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                    if (this.txtFileFragment.adapter.isFilter && (indexOf8 = this.listTxt.indexOf(fileListModel)) != -1) {
                        this.listTxt.set(indexOf8, fileListModel2);
                    }
                    this.txtFileFragment.adapter.getList().set(indexOf7, fileListModel2);
                    this.txtFileFragment.adapter.notifyItemChanged(indexOf7);
                }
                refreshList();
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
                    Toast.makeText(this, "Archive Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                this.binding.rlProgess.setVisibility(View.VISIBLE);
                this.dialogCompress.dismiss();
                this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
                    @Override
                    public final Object call() {
                        return DocumentActivity.this.m98xce0c4a14();
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
                        DocumentActivity.this.binding.rlProgess.setVisibility(View.GONE);
                        Toast.makeText(DocumentActivity.this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(this, SaveLocationActivity.class);
                intent.putExtra("comeFrom", "Copy");
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                    @Override
                    public final void onActivityResult(Object obj) {
                        DocumentActivity.this.m95xada5eab((ActivityResult) obj);
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutInfo:
                long j = 0;
                for (int i = 0; i < this.selectedList.size(); i++) {
                    File file3 = new File(this.selectedList.get(i).getFilePath());
                    if (file3.isDirectory()) {
                        for (File file4 : file3.listFiles()) {
                            j += file4.length();
                        }
                    } else {
                        j += file3.length();
                    }
                }
                BottomsheetInfoBinding bottomsheetInfoBinding = (BottomsheetInfoBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_info, null, false);
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(bottomsheetInfoBinding.getRoot());
                bottomSheetDialog.show();
                bottomsheetInfoBinding.txtFilePath.setText(this.selectedList.get(0).getFilePath());
                bottomsheetInfoBinding.txtTime.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.selectedList.get(0).getFileDate())));
                bottomsheetInfoBinding.txtFileSize.setText("" + getFolderSizeLabel(j));
                bottomsheetInfoBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        bottomSheetDialog.dismiss();
                    }
                });
                this.dialogMultiBottomSheet.dismiss();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutMove:
                Intent intent2 = new Intent(this, SaveLocationActivity.class);
                intent2.putExtra("comeFrom", "Move");
                this.activityLauncher.launch(intent2, new BetterActivityResult.OnActivityResult() {
                    @Override
                    public final void onActivityResult(Object obj) {
                        DocumentActivity.this.m96x9815102c((ActivityResult) obj);
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
                for (int i2 = 0; i2 < this.selectedList.size(); i2++) {
                    arrayList.add(this.selectedList.get(i2).getFilePath());
                    File file5 = new File((String) arrayList.get(i2));
                    if (file5.isDirectory()) {
                        for (File file6 : file5.listFiles()) {
                            arrayList.add(file6.getPath());
                        }
                    }
                }
                AppConstants.share(this, arrayList);
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.layoutrenam:
                renameDialog();
                this.dialogMultiBottomSheet.dismiss();
                return;
            case R.id.ll:
                this.activityLauncher.launch(new Intent(this, SaveLocationActivity.class), new BetterActivityResult.OnActivityResult() {
                    @Override
                    public final void onActivityResult(Object obj) {
                        DocumentActivity.this.m99x5b46fb95((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llCompress:
                Intent intent3 = new Intent(this, CompressActivity.class);
                intent3.putExtra("selectedFileListModel", this.selectedList);
                intent3.putExtra("isFile", false);
                this.activityLauncher.launch(intent3, new BetterActivityResult.OnActivityResult() {
                    @Override
                    public final void onActivityResult(Object obj) {
                        DocumentActivity.this.m97x40d19893((ActivityResult) obj);
                    }
                });
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
        for (int i3 = 0; i3 < this.selectedList.size(); i3++) {
            File file7 = new File(this.selectedList.get(i3).getFilePath());
            try {
                PDFFileFragment pDFFileFragment2 = this.pdfFileFragment;
                if (pDFFileFragment2 != null && pDFFileFragment2.adapter != null) {
                    int indexOf19 = this.pdfFileFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf19 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.pdfFileFragment.adapter.getList().remove(indexOf19);
                    }
                    if (this.pdfFileFragment.adapter.isFilter && (indexOf6 = this.listPdf.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listPdf.remove(indexOf6);
                    }
                    this.pdfFileFragment.checkNoData();
                }
                TXTFileFragment tXTFileFragment2 = this.txtFileFragment;
                if (tXTFileFragment2 != null && tXTFileFragment2.adapter != null) {
                    int indexOf20 = this.txtFileFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf20 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.txtFileFragment.adapter.getList().remove(indexOf20);
                    }
                    if (this.txtFileFragment.adapter.isFilter && (indexOf5 = this.listTxt.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listTxt.remove(indexOf5);
                    }
                    this.txtFileFragment.checkNoData();
                }
                WordFilesFragment wordFilesFragment2 = this.wordFilesFragment;
                if (wordFilesFragment2 != null && wordFilesFragment2.adapter != null) {
                    int indexOf21 = this.wordFilesFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf21 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.wordFilesFragment.adapter.getList().remove(indexOf21);
                    }
                    if (this.wordFilesFragment.adapter.isFilter && (indexOf4 = this.listWord.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listWord.remove(indexOf4);
                    }
                    this.wordFilesFragment.checkNoData();
                }
                ExcelFilesFragment excelFilesFragment2 = this.excelFilesFragment;
                if (excelFilesFragment2 != null && excelFilesFragment2.adapter != null) {
                    int indexOf22 = this.excelFilesFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf22 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.excelFilesFragment.adapter.getList().remove(indexOf22);
                    }
                    if (this.excelFilesFragment.adapter.isFilter && (indexOf3 = this.listExcel.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listExcel.remove(indexOf3);
                    }
                    this.excelFilesFragment.checkNoData();
                }
                PPTFileFragment pPTFileFragment2 = this.pptFileFragment;
                if (pPTFileFragment2 != null && pPTFileFragment2.adapter != null) {
                    int indexOf23 = this.pptFileFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf23 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.pptFileFragment.adapter.getList().remove(indexOf23);
                    }
                    if (this.pptFileFragment.adapter.isFilter && (indexOf2 = this.listPpt.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listPpt.remove(indexOf2);
                    }
                    this.pptFileFragment.checkNoData();
                }
                AllFileShowFragment allFileShowFragment2 = this.allFileShowFragment;
                if (allFileShowFragment2 != null && allFileShowFragment2.adapter != null) {
                    int indexOf24 = this.allFileShowFragment.adapter.getList().indexOf(this.selectedList.get(i3));
                    if (indexOf24 != -1) {
                        ZipManager.doDelete(file7);
                        AppConstants.refreshGallery(file7.getAbsolutePath(), this);
                        this.allFileShowFragment.adapter.getList().remove(indexOf24);
                    }
                    if (this.allFileShowFragment.adapter.isFilter && (indexOf = this.listAll.indexOf(this.selectedList.get(i3))) != -1) {
                        this.listAll.remove(indexOf);
                    }
                    this.allFileShowFragment.checkNoData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        refreshList();
        this.deleteDialog.dismiss();
    }


    public void m97x40d19893(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Toast.makeText(this, "Zip File Successfully", Toast.LENGTH_SHORT).show();
    }


    public Boolean m98xce0c4a14() {
        zipFileFolders();
        return true;
    }


    public void m99x5b46fb95(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.dialogCompressBinding.edSaveLocation.setText(activityResult.getData().getStringExtra("path"));
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
    }


    public void m95xada5eab(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        for (int i = 0; i < this.selectedList.size(); i++) {
            try {
                ZipManager.copy(this.selectedList.get(i).getFilePath(), this.ROOT_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        this.binding.rlProgess.setVisibility(View.GONE);
        refreshList();
        Toast.makeText(this, "Copied Files Successfully", Toast.LENGTH_SHORT).show();
    }


    public void m96x9815102c(ActivityResult activityResult) {
        int indexOf;
        int indexOf2;
        int indexOf3;
        int indexOf4;
        int indexOf5;
        int indexOf6;
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.ROOT_PATH = activityResult.getData().getStringExtra("path");
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        for (int i = 0; i < this.selectedList.size(); i++) {
            try {
                if (!new File(this.ROOT_PATH + InternalZipConstants.ZIP_FILE_SEPARATOR + this.selectedList.get(i).getFilename()).exists()) {
                    ZipManager.move(this.selectedList.get(i).getFilePath(), this.ROOT_PATH);
                    FileListModel fileListModel = new FileListModel();
                    fileListModel.setFilePath(this.selectedList.get(i).getFilePath());
                    AppConstants.refreshGallery(this.ROOT_PATH + this.selectedList.get(i).getFilename(), this);
                    this.selectedList.get(i).setFilePath(this.ROOT_PATH + this.selectedList.get(i).getFilename());
                    FileListModel fileListModel2 = this.selectedList.get(i);
                    if (this.allFileShowFragment.adapter != null && (indexOf6 = this.allFileShowFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.allFileShowFragment.adapter.getList().set(indexOf6, fileListModel2);
                        this.allFileShowFragment.adapter.notifyItemChanged(indexOf6);
                    }
                    if (this.pdfFileFragment.adapter != null && (indexOf5 = this.pdfFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.pdfFileFragment.adapter.getList().set(indexOf5, fileListModel2);
                        this.pdfFileFragment.adapter.notifyItemChanged(indexOf5);
                    }
                    if (this.wordFilesFragment.adapter != null && (indexOf4 = this.wordFilesFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.wordFilesFragment.adapter.getList().set(indexOf4, fileListModel2);
                        this.wordFilesFragment.adapter.notifyItemChanged(indexOf4);
                    }
                    if (this.excelFilesFragment.adapter != null && (indexOf3 = this.excelFilesFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.excelFilesFragment.adapter.getList().set(indexOf3, fileListModel2);
                        this.excelFilesFragment.adapter.notifyItemChanged(indexOf3);
                    }
                    if (this.pptFileFragment.adapter != null && (indexOf2 = this.pptFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.pptFileFragment.adapter.getList().set(indexOf2, fileListModel2);
                        this.pptFileFragment.adapter.notifyItemChanged(indexOf2);
                    }
                    if (this.txtFileFragment.adapter != null && (indexOf = this.txtFileFragment.adapter.getList().indexOf(fileListModel)) != -1) {
                        this.txtFileFragment.adapter.getList().set(indexOf, fileListModel2);
                        this.txtFileFragment.adapter.notifyItemChanged(indexOf);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
        this.item = menu.findItem(R.id.search);
        return super.onCreateOptionsMenu(menu);
    }

    private void search(final SearchView searchView) {
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHint("Search");
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.white));
        ImageView imageView = (ImageView) searchView.findViewById(R.id.search_close_btn);
        imageView.setImageResource(R.drawable.ic_close);
        EditText editText = (EditText) searchView.findViewById(R.id.search_src_text);
        editText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        editText.setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentActivity.this.setDocsData();
                if (searchView.isIconified()) {
                    return;
                }
                searchView.setIconified(true);
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    return;
                }
                DocumentActivity.this.setDocsData();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                if (str.length() > 0) {
                    if (DocumentActivity.this.activeFragment instanceof AllFileShowFragment) {
                        DocumentActivity.this.allFileShowFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else if (DocumentActivity.this.activeFragment instanceof WordFilesFragment) {
                        DocumentActivity.this.wordFilesFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else if (DocumentActivity.this.activeFragment instanceof ExcelFilesFragment) {
                        DocumentActivity.this.excelFilesFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else if (DocumentActivity.this.activeFragment instanceof PPTFileFragment) {
                        DocumentActivity.this.pptFileFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else if (DocumentActivity.this.activeFragment instanceof PDFFileFragment) {
                        DocumentActivity.this.pdfFileFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else if (DocumentActivity.this.activeFragment instanceof TXTFileFragment) {
                        DocumentActivity.this.txtFileFragment.adapter.getFilter().filter(str, new Filter.FilterListener() {
                            @Override
                            public void onFilterComplete(int i) {
                            }
                        });
                        return false;
                    } else {
                        return false;
                    }
                }
                DocumentActivity.this.setDocsData();
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                DocumentActivity.this.setDocsData();
                return false;
            }
        });
    }

    public void setDocsData() {
        Fragment fragment = this.activeFragment;
        if (fragment instanceof AllFileShowFragment) {
            this.allFileShowFragment.adapter.setMainList(this.listAll);
        } else if (fragment instanceof WordFilesFragment) {
            this.wordFilesFragment.adapter.setMainList(this.listWord);
        } else if (fragment instanceof ExcelFilesFragment) {
            this.excelFilesFragment.adapter.setMainList(this.listExcel);
        } else if (fragment instanceof PPTFileFragment) {
            this.pptFileFragment.adapter.setMainList(this.listPpt);
        } else if (fragment instanceof PDFFileFragment) {
            this.pdfFileFragment.adapter.setMainList(this.listPdf);
        } else if (fragment instanceof TXTFileFragment) {
            this.txtFileFragment.adapter.setMainList(this.listTxt);
        }
    }


    public class ViewPagerFragmentAdapter extends FragmentStateAdapter {
        private List<Fragment> listFragment;

        public ViewPagerFragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, List<Fragment> list) {
            super(fragmentManager, lifecycle);
            this.listFragment = list;
        }

        @Override
        public Fragment createFragment(int i) {
            return this.listFragment.get(i);
        }

        @Override
        public int getItemCount() {
            return DocumentActivity.this.tabTitle.length;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}





































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































