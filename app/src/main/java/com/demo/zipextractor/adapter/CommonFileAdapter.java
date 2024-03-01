package com.demo.zipextractor.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
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
import com.demo.zipextractor.databinding.ItemShowAllFilesBinding;
import com.demo.zipextractor.utils.AppPref;
import com.demo.zipextractor.utils.CheakBoxClick;
import com.demo.zipextractor.utils.MainConstant;
import com.demo.zipextractor.utils.RecyclerItemClick;
import com.demo.zipextractor.model.FileListModel;
import com.demo.zipextractor.utils.AppConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class CommonFileAdapter extends RecyclerView.Adapter<CommonFileAdapter.ViewHolder> implements Filterable {
    CheakBoxClick cheakBoxClick;
    ArrayList<FileListModel> filterList;
    public boolean isFilter = false;
    public boolean isSelectAll = false;
    RecyclerItemClick itemClick;
    ArrayList<FileListModel> list;
    Context mContext;
    FileListModel model;
    ArrayList<FileListModel> selectedList;

    public CommonFileAdapter(Context context, ArrayList<FileListModel> arrayList, ArrayList<FileListModel> arrayList2, RecyclerItemClick recyclerItemClick, CheakBoxClick cheakBoxClick) {
        this.mContext = context;
        this.list = arrayList;
        this.filterList = arrayList;
        this.selectedList = arrayList2;
        this.itemClick = recyclerItemClick;
        this.cheakBoxClick = cheakBoxClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_show_all_files, viewGroup, false));
    }

    public FileListModel getModelByPosition(int i) {
        return this.filterList.get(i);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        this.model = this.filterList.get(i);
        Log.e("File name:", this.model.getFilename() + ", Mime Type:=" + this.model.getFileType());
        if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_ZIP) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_TAR) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_7Z) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_RAR)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.zip_file));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_APK)) {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.apk));
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_AUDIO) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_WAV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_OGG) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_AIFF) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_FLC) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_ALAC) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_WMA) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_AAC)) {
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
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PICTURE_JPEG) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PICTURE) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_PNG) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_TIFF) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_EPS) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_BITMAP) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_GIF) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_TIF)) {
            if (!AppPref.IsThumbnail(this.mContext)) {
                viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.picture));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.binding.iconView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.fab_color)));
                }
            } else {
                Glide.with(this.mContext).load(this.model.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().override(200, 200).transform(new CenterCrop(), new RoundedCorners(20))).into(viewHolder.binding.iconView);
            }
        } else if (this.model.getFilename().endsWith(MainConstant.FILE_TYPE_MP4) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_MOV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_MKV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_WMV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_M2TS) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_FLV) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_F4V) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_SWF) || this.model.getFilename().endsWith(MainConstant.FILE_TYPE_AVI)) {
            if (!AppPref.IsThumbnail(this.mContext)) {
                viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_video));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.binding.iconView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.fab_color)));
                }
            } else {
                Glide.with(this.mContext).load(this.model.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().override(200, 200).transform(new CenterCrop(), new RoundedCorners(20))).into(viewHolder.binding.iconView);
            }
        } else {
            viewHolder.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_thmb_audio));
        }
        viewHolder.binding.fileDate.setText(getFileDate());
        viewHolder.binding.fileSize.setText(AppConstants.formatFileSize(this.model.getFileSize()));
        if (this.selectedList.contains(this.model)) {
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
                    CommonFileAdapter.this.isFilter = true;
                    ArrayList arrayList = new ArrayList();
                    Iterator<FileListModel> it = CommonFileAdapter.this.list.iterator();
                    while (it.hasNext()) {
                        FileListModel next = it.next();
                        if (next.getFilename().toLowerCase().contains(trim.toString().toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                } else {
                    CommonFileAdapter.this.isFilter = false;
                    filterResults.values = CommonFileAdapter.this.list;
                    filterResults.count = CommonFileAdapter.this.list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                CommonFileAdapter.this.filterList = (ArrayList) filterResults.values;
                CommonFileAdapter.this.notifyDataSetChanged();
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
                    CommonFileAdapter.this.itemClick.onRecyclerClick(ViewHolder.this.getAdapterPosition());
                }
            });
            this.binding.llCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    if (CommonFileAdapter.this.selectedList.contains(CommonFileAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()))) {
                        CommonFileAdapter.this.selectedList.remove(CommonFileAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(false);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(CommonFileAdapter.this.mContext, R.color.main_bg));
                    } else {
                        CommonFileAdapter.this.selectedList.add(CommonFileAdapter.this.filterList.get(ViewHolder.this.getAdapterPosition()));
                        ViewHolder.this.binding.checkBox.setChecked(true);
                        ViewHolder.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(CommonFileAdapter.this.mContext, R.color.selected_card));
                    }
                    CommonFileAdapter.this.cheakBoxClick.onCheakBoxClick(ViewHolder.this.getAdapterPosition(), CommonFileAdapter.this.selectedList);
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

    public void removeItem(ArrayList<FileListModel> arrayList, int i) {
        this.filterList.remove(arrayList);
        notifyItemRemoved(i);
    }

    public void selectAll() {
        this.isSelectAll = true;
        notifyDataSetChanged();
    }

    public void unselectall() {
        this.isSelectAll = false;
        notifyDataSetChanged();
    }
}
