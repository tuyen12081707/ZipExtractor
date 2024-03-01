package com.demo.zipextractor.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.demo.zipextractor.adapter.AllDocsAdapter;
import com.demo.zipextractor.adapter.AllFileAdapter;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.zipextractor.R;
import com.demo.zipextractor.activity.DocumentActivity;
import com.demo.zipextractor.databinding.BottomsheetlayoutMultipleBinding;
import com.demo.zipextractor.databinding.FragmentAllFileShowBinding;

import io.reactivex.disposables.CompositeDisposable;

import java.io.File;
import java.util.ArrayList;


public class AllFileShowFragment extends Fragment implements RecyclerItemClick, CheakBoxClick, View.OnClickListener {
    public AllDocsAdapter adapter;
    FragmentAllFileShowBinding binding;
    BottomsheetlayoutMultipleBinding bottomsheetlayoutMultipleBinding;
    BottomSheetDialog dialogMultiBottomSheet;
    CompositeDisposable disposable;


    @Override
    public void onCheakBoxClick(int i, ArrayList<FileListModel> arrayList) {
    }

    @Override
    public void onClick(View view) {
    }

    Activity activity = null;

    public AllFileShowFragment(Activity activity) {
        this.activity = activity;
    }



    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = (FragmentAllFileShowBinding) DataBindingUtil.inflate(layoutInflater, R.layout.fragment_all_file_show, viewGroup, false);
        initView();
        initClick();
        return this.binding.getRoot();
    }

    private void initClick() {
        this.binding.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFileShowFragment.this.ShowMultiBottomSheetDialog();
            }
        });
        this.bottomsheetlayoutMultipleBinding.layoutCopy.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMove.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutInfo.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiShare.setOnClickListener(this);
        this.bottomsheetlayoutMultipleBinding.layoutMultiDelete.setOnClickListener(this);
    }

    public void initView() {
        this.disposable = new CompositeDisposable();
        this.bottomsheetlayoutMultipleBinding = (BottomsheetlayoutMultipleBinding) DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.bottomsheetlayout_multiple, null, false);
        this.dialogMultiBottomSheet = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        setAdapter();
    }

    public void setAdapter() {
        try {

            Log.e("MYTAG", "ErrorNo: activity():" + activity);
            Log.e("MYTAG", "ErrorNo: listAll:" + ((DocumentActivity) activity).listAll);
            Log.e("MYTAG", "ErrorNo: setAdapter:" + ((DocumentActivity) activity).selectedList);
            this.adapter = new AllDocsAdapter(activity, ((DocumentActivity) activity).listAll, ((DocumentActivity) activity).selectedList, this, (CheakBoxClick) getActivity());
            this.binding.rvAllList.setLayoutManager(new LinearLayoutManager(getActivity()));
            this.binding.rvAllList.setAdapter(this.adapter);
            this.binding.rvAllList.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.item_animation));
            setAllData();
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


    public void ShowMultiBottomSheetDialog() {
        this.dialogMultiBottomSheet.setContentView(this.bottomsheetlayoutMultipleBinding.getRoot());
        this.dialogMultiBottomSheet.show();
    }

    @Override
    public void onRecyclerClick(int i) {
        ((DocumentActivity) getActivity()).isClicked = true;
        AllFileAdapter.openFile(getActivity(), this.adapter.getModelByPosition(i).getFilePath(), new File(this.adapter.getModelByPosition(i).getFilePath()));
    }

    public void setAllData() {
        this.adapter.setList(((DocumentActivity) getActivity()).listAll);
    }

    public void checkListSize() {
        this.binding.llNoData.setVisibility(this.adapter.getList().size() > 0 ? View.GONE : View.VISIBLE);
    }
}
