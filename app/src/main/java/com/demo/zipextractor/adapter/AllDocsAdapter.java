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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.demo.zipextractor.R;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.MainConstant;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.activity.DocumentActivity;
import com.demo.zipextractor.databinding.ItemShowAllFilesBinding;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class AllDocsAdapter extends RecyclerView.Adapter<AllDocsAdapter.ViewHolder> implements Filterable {
    CheakBoxClick cheakBoxClick;
    ArrayList<FileListModel> filterList;
    public boolean isFilter = false;
    RecyclerItemClick itemClick;
    ArrayList<FileListModel> list;
    Context mContext;
    FileListModel model;

    public AllDocsAdapter() {
    }

    public AllDocsAdapter(Context context, ArrayList<FileListModel> arrayList, ArrayList<FileListModel> arrayList2, RecyclerItemClick recyclerItemClick, CheakBoxClick cheakBoxClick) {
        this.mContext = context;
        this.list = arrayList;
        this.filterList = arrayList;
        this.itemClick = recyclerItemClick;
        this.cheakBoxClick = cheakBoxClick;
    }

    public FileListModel getModelByPosition(int i) {
        return this.filterList.get(i);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_show_all_files, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        FileListModel fileListModel = this.filterList.get(i);
        this.model = fileListModel;
        if (fileListModel.getFilename().endsWith(MainConstant.FILE_TYPE_ZIP) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_TAR) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_7Z)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.zip_file));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_APK)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.apk));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_AUDIO) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_WAV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_OGG)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_thmb_audio));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_DOC) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_DOCX)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.doc));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_XLS) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_XLSX)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.xls));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PDF) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PDF_CAPS)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.pdf));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PPT) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PPTX)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ppt));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_TXT) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_HTML) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_XML) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_RTF)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.txt));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PICTURE) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PICTURE_JPEG) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PNG) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_MP4)) {
            Glide.with(this.mContext).load(this.model.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().override(200, 200).transform(new CenterCrop(), new RoundedCorners(20))).into(viewHolder.binding.iconView);
        }
        viewHolder.binding.fileDate.setText(getFileDate());
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
                ArrayList arrayList = new ArrayList();
                if (trim.length() > 0) {
                    AllDocsAdapter.this.isFilter = true;
                    Iterator<FileListModel> it = AllDocsAdapter.this.list.iterator();
                    while (it.hasNext()) {
                        FileListModel next = it.next();
                        if (next.getFilename().toLowerCase().contains(trim.toString().toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                } else {
                    AllDocsAdapter.this.isFilter = false;
                    filterResults.values = AllDocsAdapter.this.list;
                    filterResults.count = AllDocsAdapter.this.list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                AllDocsAdapter.this.filterList = (ArrayList) filterResults.values;
                AllDocsAdapter.this.notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShowAllFilesBinding binding;

        public ViewHolder(View view) {
            super(view);
            this.binding= (ItemShowAllFilesBinding) DataBindingUtil.bind(view);
            this.binding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    AllDocsAdapter.this.itemClick.onRecyclerClick(ViewHolder.this.getAdapterPosition());
                }
            });
            this.binding.llCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    if (((DocumentActivity) AllDocsAdapter.this.mContext).selectedList.contains(AllDocsAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()))) {
                        ((DocumentActivity) AllDocsAdapter.this.mContext).selectedList.remove(AllDocsAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(false);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(AllDocsAdapter.this.mContext, R.color.main_bg));
                    } else {
                        ((DocumentActivity) AllDocsAdapter.this.mContext).selectedList.add(AllDocsAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(true);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(AllDocsAdapter.this.mContext, R.color.selected_card));
                    }
                    AllDocsAdapter.this.cheakBoxClick.onCheakBoxClick(ViewHolder.this.getAdapterPosition(), AllDocsAdapter.this.list);
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
        try {
            if(this.filterList!=null){
                return this.filterList;
            }else {
                return new ArrayList<FileListModel>();
            }
        }catch (Exception e){
            return new ArrayList<FileListModel>();
        }
    }

    public void removeItem(ArrayList<FileListModel> arrayList, int i) {
        this.filterList.remove(arrayList);
        notifyItemRemoved(i);
    }

    public void setMainList(ArrayList<FileListModel> arrayList) {

        this.filterList = arrayList;
        notifyDataSetChanged();
    }

    public void removeItem(int i) {
        if (i <= this.filterList.size() - 1) {
            this.filterList.remove(i);
            notifyItemRemoved(i);
        }
    }
}
