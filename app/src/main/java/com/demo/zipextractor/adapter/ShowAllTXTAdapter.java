package com.demo.zipextractor.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.zipextractor.R;
import com.demo.zipextractor.activity.DocumentActivity;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.databinding.ItemShowAllFilesBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class ShowAllTXTAdapter extends RecyclerView.Adapter<ShowAllTXTAdapter.ViewHolder> implements Filterable {
    CheakBoxClick cheakBoxClick;
    ArrayList<FileListModel> filterList;
    RecyclerItemClick itemClick;
    ArrayList<FileListModel> list;
    ArrayList<FileListModel> listCheakbox;
    Context mContext;
    FileListModel model;

    public ShowAllTXTAdapter(Context context, ArrayList<FileListModel> arrayList, ArrayList<FileListModel> arrayList2, RecyclerItemClick recyclerItemClick, CheakBoxClick cheakBoxClick) {
        this.mContext = context;
        this.list = arrayList;
        this.filterList = arrayList;
        this.listCheakbox = arrayList2;
        this.itemClick = recyclerItemClick;
        this.cheakBoxClick = cheakBoxClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_show_all_files, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        this.model = this.filterList.get(i);
        viewHolder.binding.fileDate.setText(getFileDate());
        viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.txt));
        viewHolder.binding.fileSize.setText(AppConstants.formatFileSize(this.model.getFileSize()));
        viewHolder.binding.setFileListModel(this.model);
        if (((DocumentActivity) this.mContext).selectedList.contains(this.model)) {
            viewHolder.binding.checkBox.setChecked(true);
            viewHolder.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.selected_card));
            return;
        }
        viewHolder.binding.checkBox.setChecked(false);
        viewHolder.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.main_bg));
    }

    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
    }

    @Override
    public int getItemCount() {
        return this.filterList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                String trim = charSequence.toString().trim();
                Filter.FilterResults filterResults = new Filter.FilterResults();
                new ArrayList();
                if (trim.length() > 0) {
                    ArrayList arrayList = new ArrayList();
                    Iterator<FileListModel> it = ShowAllTXTAdapter.this.list.iterator();
                    while (it.hasNext()) {
                        FileListModel next = it.next();
                        if (next.getFilename().toLowerCase().contains(trim.toString().toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                } else {
                    filterResults.values = ShowAllTXTAdapter.this.list;
                    filterResults.count = ShowAllTXTAdapter.this.list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ShowAllTXTAdapter.this.filterList = (ArrayList) filterResults.values;
                ShowAllTXTAdapter.this.notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShowAllFilesBinding binding;

        public ViewHolder(View view) {
            super(view);
            ItemShowAllFilesBinding itemShowAllFilesBinding = (ItemShowAllFilesBinding) DataBindingUtil.bind(view);
            this.binding = itemShowAllFilesBinding;
            itemShowAllFilesBinding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    ShowAllTXTAdapter.this.itemClick.onRecyclerClick(ViewHolder.this.getAdapterPosition());
                }
            });
            this.binding.llCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    if (((DocumentActivity) ShowAllTXTAdapter.this.mContext).selectedList.contains(ShowAllTXTAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()))) {
                        ((DocumentActivity) ShowAllTXTAdapter.this.mContext).selectedList.remove(ShowAllTXTAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(false);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(ShowAllTXTAdapter.this.mContext, R.color.main_bg));
                    } else {
                        ((DocumentActivity) ShowAllTXTAdapter.this.mContext).selectedList.add(ShowAllTXTAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(true);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(ShowAllTXTAdapter.this.mContext, R.color.selected_card));
                    }
                    ShowAllTXTAdapter.this.cheakBoxClick.onCheakBoxClick(ViewHolder.this.getAdapterPosition(), ShowAllTXTAdapter.this.list);
                }
            });
        }
    }

    public String getFileDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.model.getFileDate()));
    }

    public Bitmap StringToBitMap(String str) {
        try {
            byte[] decode = Base64.decode(str, 0);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public FileListModel getModelByPosition(int i) {
        return this.filterList.get(i);
    }

    public void setList(ArrayList<FileListModel> arrayList) {
        this.filterList = arrayList;
        notifyDataSetChanged();
    }

    public ArrayList<FileListModel> getList() {
        return this.filterList;
    }

    public void removeItem(int i) {
        if (i <= this.filterList.size() - 1) {
            this.filterList.remove(i);
            notifyItemRemoved(i);
        }
    }

    public void setMainList(ArrayList<FileListModel> arrayList) {
        this.filterList = arrayList;
        notifyDataSetChanged();
    }
}
