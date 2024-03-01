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
import com.demo.zipextractor.databinding.ItemShowAllFilesBinding;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.RecyclerItemClick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class ShowAllPdfAdapter extends RecyclerView.Adapter<ShowAllPdfAdapter.ViewHolder> implements Filterable {
    CheakBoxClick cheakBoxClick;
    ArrayList<FileListModel> filterList;
    public boolean isFilter = false;
    RecyclerItemClick itemClick;
    ArrayList<FileListModel> list;
    Context mContext;
    FileListModel model;

    public ShowAllPdfAdapter(Context context, ArrayList<FileListModel> arrayList, ArrayList<FileListModel> arrayList2, RecyclerItemClick recyclerItemClick, CheakBoxClick cheakBoxClick) {
        this.mContext = context;
        this.list = arrayList;
        this.filterList = arrayList;
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
        viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.pdf));
        viewHolder.binding.fileSize.setText(AppConstants.formatFileSize(this.model.getFileSize()));
        if (((DocumentActivity) this.mContext).selectedList.contains(this.model)) {
            viewHolder.binding.checkBox.setChecked(true);
            viewHolder.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.selected_card));
        } else {
            viewHolder.binding.checkBox.setChecked(false);
            viewHolder.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.main_bg));
        }
        viewHolder.binding.setFileListModel(this.model);
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
                    ShowAllPdfAdapter.this.isFilter = true;
                    ArrayList arrayList = new ArrayList();
                    Iterator<FileListModel> it = ShowAllPdfAdapter.this.list.iterator();
                    while (it.hasNext()) {
                        FileListModel next = it.next();
                        if (next.getFilename().toLowerCase().contains(trim.toString().toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                } else {
                    ShowAllPdfAdapter.this.isFilter = false;
                    filterResults.values = ShowAllPdfAdapter.this.list;
                    filterResults.count = ShowAllPdfAdapter.this.list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ShowAllPdfAdapter.this.filterList = (ArrayList) filterResults.values;
                ShowAllPdfAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public FileListModel getModelByPosition(int i) {
        return this.filterList.get(i);
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
                    ShowAllPdfAdapter.this.itemClick.onRecyclerClick(ViewHolder.this.getAdapterPosition());
                }
            });
            this.binding.llCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    if (((DocumentActivity) ShowAllPdfAdapter.this.mContext).selectedList.contains(ShowAllPdfAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()))) {
                        ((DocumentActivity) ShowAllPdfAdapter.this.mContext).selectedList.remove(ShowAllPdfAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(false);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(ShowAllPdfAdapter.this.mContext, R.color.main_bg));
                    } else {
                        ((DocumentActivity) ShowAllPdfAdapter.this.mContext).selectedList.add(ShowAllPdfAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(true);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(ShowAllPdfAdapter.this.mContext, R.color.selected_card));
                    }
                    ShowAllPdfAdapter.this.cheakBoxClick.onCheakBoxClick(ViewHolder.this.getAdapterPosition(), ShowAllPdfAdapter.this.list);
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
            notifyDataSetChanged();
        }
    }

    public void setMainList(ArrayList<FileListModel> arrayList) {
        this.filterList = arrayList;
        notifyDataSetChanged();
    }
}
