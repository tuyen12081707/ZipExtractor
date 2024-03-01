package com.demo.zipextractor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.zipextractor.R;
import com.demo.zipextractor.databinding.ItemPathBinding;
import com.demo.zipextractor.utils.RecyclerItemClick;

import java.util.ArrayList;


public class NavPathAdapter extends RecyclerView.Adapter<NavPathAdapter.MyView> {
    RecyclerItemClick itemClick;
    Context mContext;
    ArrayList<String> navPathList;

    public NavPathAdapter(Context context, ArrayList<String> arrayList, RecyclerItemClick recyclerItemClick) {
        this.mContext = context;
        this.navPathList = arrayList;
        this.itemClick = recyclerItemClick;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyView(LayoutInflater.from(this.mContext).inflate(R.layout.item_path, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MyView myView, int i) {
        String str = this.navPathList.get(i);
        if (i == this.navPathList.size() - 1) {
            myView.binding.textview.setText(str);
        } else {
            myView.binding.textview.setText(str + "  >  ");
        }
    }

    @Override
    public int getItemCount() {
        return this.navPathList.size();
    }


    public class MyView extends RecyclerView.ViewHolder {
        ItemPathBinding binding;

        public MyView(View view) {
            super(view);
            this.binding = (ItemPathBinding) DataBindingUtil.bind(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    NavPathAdapter.this.itemClick.onRecyclerClick(MyView.this.getAdapterPosition());
                }
            });
        }
    }

    public void setList(ArrayList<String> arrayList) {
        this.navPathList = arrayList;
        notifyDataSetChanged();
    }
}
