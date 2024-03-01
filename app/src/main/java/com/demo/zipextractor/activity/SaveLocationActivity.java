package com.demo.zipextractor.activity;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

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
import com.demo.zipextractor.databinding.ActivitySaveLocationBinding;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;


public class SaveLocationActivity extends BaseActivity implements AllFileAdapter.FragmentCommunication, View.OnClickListener, RecyclerItemClick {
    SaveLocationActivity activity;
    AllFileAdapter adapter;
    NavPathAdapter adapterNav;
    ActivitySaveLocationBinding binding;
    CompositeDisposable disposable;
    ArrayList<String> navPathList;
    File root;
    String rootPath;
    ArrayList<File> fileList = new ArrayList<>();
    ArrayList<FileListModel> list = new ArrayList<>();
    StringBuilder navPath = new StringBuilder();
    int itemPos = 0;

    @Override
    public void setBinding() {
        this.binding = (ActivitySaveLocationBinding) DataBindingUtil.setContentView(this, R.layout.activity_save_location);

        this.activity = this;
    }

    @Override
    public void setToolBar() {
        this.binding.toolBarAllfile.title.setText("Select Destination Path");
        this.binding.toolBarAllfile.cardBack.setOnClickListener(this);
    }

    @Override
    public void initMethod() {
        initView();
        initClick();
        fillData();
        setAdapternavPath();
    }

    private void setAdapternavPath() {
        this.adapterNav = new NavPathAdapter(this, this.navPathList, this);
        this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.recyclerView.setAdapter(this.adapterNav);
    }

    private void initView() {
        this.rootPath = Environment.getExternalStorageDirectory().getPath() + InternalZipConstants.ZIP_FILE_SEPARATOR;
        this.navPathList = new ArrayList<>();
        this.root = new File(this.rootPath);
        this.disposable = new CompositeDisposable();
        setButtonText();
        this.navPath.append("Internal Storage");
        this.binding.toolBarAllfile.title.setText(this.navPath);
        this.navPathList.add(0, "Internal Storage");
    }

    private void setButtonText() {
        String stringExtra = getIntent().getStringExtra("comeFrom");
        if (stringExtra != null) {
            stringExtra.hashCode();
            char c = 65535;
            switch (stringExtra.hashCode()) {
                case 2106261:
                    if (stringExtra.equals("Copy")) {
                        c = 0;
                        break;
                    }
                    break;
                case 2404337:
                    if (stringExtra.equals("Move")) {
                        c = 1;
                        break;
                    }
                    break;
                case 2569629:
                    if (stringExtra.equals("Save")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    this.binding.txtAction.setText("Copy Here");
                    return;
                case 1:
                    this.binding.txtAction.setText("Move Here");
                    return;
                case 2:
                    this.binding.txtAction.setText("Save Here");
                    return;
                default:
                    return;
            }
        }
    }

    private void fillData() {
        this.binding.rlProgess.setVisibility(View.VISIBLE);
        this.disposable.add((Disposable) Observable.fromCallable(new Callable() {
            @Override
            public final Object call() {
                return SaveLocationActivity.this.m112xb45aa4c1();
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
                SaveLocationActivity.this.binding.rlProgess.setVisibility(View.GONE);
                SaveLocationActivity.this.setAdapter();
            }
        }));
    }


    public Boolean m112xb45aa4c1() {
        ArrayList<File> arrayList = (ArrayList) AppConstants.convertArrayToList(new File(this.rootPath).listFiles((FileFilter) FileFilterUtils.directoryFileFilter()));
        this.fileList = arrayList;
        Iterator<File> it = arrayList.iterator();
        while (it.hasNext()) {
            File next = it.next();
            this.list.add(new FileListModel(next.getPath(), next.getName(), next.length(), next.lastModified(), "", ""));
        }
        return true;
    }

    private void initClick() {
        this.binding.btnCancelLocation.setOnClickListener(this);
        this.binding.btnSaveLocaion.setOnClickListener(this);
    }


    public void setAdapter() {
        ArrayList<File> arrayList = this.fileList;
        this.adapter = new AllFileAdapter(this, arrayList, arrayList, null, this, true);
        this.binding.rvAllFile.setLayoutManager(new LinearLayoutManager(this));
        this.binding.rvAllFile.setAdapter(this.adapter);
        this.binding.rvAllFile.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.item_animation));
        this.binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }

    @Override
    public void respond(String str) {
        this.rootPath = str;
        ArrayList<File> arrayList = (ArrayList) AppConstants.convertArrayToList(new File(this.rootPath).listFiles((FileFilter) FileFilterUtils.directoryFileFilter()));
        this.fileList = arrayList;
        this.adapter.setList(arrayList);
        this.navPathList.add(FilenameUtils.getName(str) + "");
        this.adapterNav.setList(this.navPathList);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCancelLocation) {
            onBackPressed();
        } else if (id == R.id.btnSaveLocaion) {
            Intent intent = getIntent();
            intent.putExtra("path", this.rootPath + InternalZipConstants.ZIP_FILE_SEPARATOR);
            setResult(-1, intent);
            finish();
        } else if (id == R.id.cardBack) {
            File file = new File(this.rootPath);
            this.root = file;
            if (!file.getParent().equalsIgnoreCase(AppConstants.ROOT_PATH)) {
                this.rootPath = new File(this.rootPath).getParent();
                ArrayList<File> arrayList = (ArrayList) AppConstants.convertArrayToList(new File(this.rootPath).listFiles((FileFilter) FileFilterUtils.directoryFileFilter()));
                this.fileList = arrayList;
                this.adapter.setList(arrayList);
                ArrayList<String> arrayList2 = this.navPathList;
                arrayList2.subList(arrayList2.size() - 1, this.navPathList.size()).clear();
                for (int i = 1; i < this.navPathList.size(); i++) {
                    this.rootPath += this.navPathList.get(i) + InternalZipConstants.ZIP_FILE_SEPARATOR;
                }
                this.adapterNav.setList(this.navPathList);
                return;
            }
            onBackPressed();
        }
    }

    @Override

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            File file = new File(this.rootPath);
            this.root = file;
            if (!file.getParent().equalsIgnoreCase(AppConstants.ROOT_PATH)) {
                File file2 = new File(this.rootPath);
                Log.e("ROOT", "onKeyDown: " + file2.getParent());
                this.rootPath = file2.getParent();
                ArrayList<File> arrayList = (ArrayList) AppConstants.convertArrayToList(new File(this.rootPath).listFiles((FileFilter) FileFilterUtils.directoryFileFilter()));
                this.fileList = arrayList;
                this.adapter.setList(arrayList);
                return false;
            }
            onBackPressed();
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRecyclerClick(int i) {
        this.itemPos = i;
        if (i > 0) {
            if (i <= this.navPathList.size()) {
                if (i != this.navPathList.size() - 1) {
                    ArrayList<String> arrayList = this.navPathList;
                    arrayList.subList(i + 1, arrayList.size()).clear();
                    this.rootPath = AppConstants.ROOT_PATH + "/0/";
                    for (int i2 = 1; i2 < this.navPathList.size(); i2++) {
                        this.rootPath += this.navPathList.get(i2) + InternalZipConstants.ZIP_FILE_SEPARATOR;
                    }
                }
            }
        } else if (i == 0) {
            this.rootPath = AppConstants.ROOT_PATH + "/0/";
            this.navPathList.clear();
            this.navPathList.add(0, "Internal Storage");
        }
        ArrayList<File> arrayList2 = (ArrayList) AppConstants.convertArrayToList(new File(this.rootPath).listFiles((FileFilter) FileFilterUtils.directoryFileFilter()));
        this.fileList = arrayList2;
        this.adapter.setList(arrayList2);
        this.adapterNav.setList(this.navPathList);
    }
}
