package com.demo.zipextractor.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.demo.zipextractor.R;
import com.demo.zipextractor.utils.AppPref;
import com.demo.zipextractor.utils.CheakBoxClickMain;
import com.demo.zipextractor.utils.MainConstant;
import com.demo.zipextractor.activity.ShowZipContentActivity;
import com.demo.zipextractor.databinding.ItemAllFileBinding;
import com.demo.zipextractor.utils.AppConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;


public class AllFileAdapter extends RecyclerView.Adapter<AllFileAdapter.MyView> implements Filterable {
    CheakBoxClickMain cheakBoxClick;
    FragmentCommunication communication;
    ArrayList<File> fileList;
    ArrayList<File> filterList;
    boolean isSecondScreen;
    Context mContext;
    File selectedFile;
    List<File> selectedFileList;
    public boolean isFilter = false;
    public boolean isSelectAll = false;
    int index = -1;


    public interface FragmentCommunication {
        void respond(String str);
    }

    public AllFileAdapter(Context context, ArrayList<File> arrayList, List<File> list, CheakBoxClickMain cheakBoxClickMain, FragmentCommunication fragmentCommunication, boolean z) {
        this.mContext = context;
        this.filterList = arrayList;
        this.communication = fragmentCommunication;
        this.fileList = arrayList;
        this.selectedFileList = list;
        this.cheakBoxClick = cheakBoxClickMain;
        this.isSecondScreen = z;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyView(LayoutInflater.from(this.mContext).inflate(R.layout.item_all_file, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MyView myView, int i) {
        bind(myView, i);
    }

    private void bind(MyView myView, int i) {
        this.selectedFile = this.filterList.get(i);
        if (this.selectedFileList.size() > 0) {
            int indexOf = this.selectedFileList.indexOf(this.selectedFile);
            this.index = indexOf;
            if (indexOf != -1) {
                myView.binding.checkBox.setChecked(true);
                myView.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.selected_card));
            } else {
                myView.binding.checkBox.setChecked(false);
                myView.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.main_bg));
            }
        } else {
            myView.binding.checkBox.setChecked(false);
            myView.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.main_bg));
        }
        if (this.isSecondScreen) {
            myView.binding.llCheckBox.setVisibility(View.GONE);
            myView.binding.llMain.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.main_bg));
        } else {
            myView.binding.llCheckBox.setVisibility(View.VISIBLE);
        }
        myView.binding.fileNameTextView.setText(this.selectedFile.getName());
        if (this.selectedFile.isDirectory()) {
            Glide.with(this.mContext).load(Integer.valueOf((int) R.drawable.folder)).into(myView.binding.iconView);
            myView.binding.fileSize.setText(getDirFileCount());
            myView.binding.fileDate.setText(getDirFileDate());
        } else {
            myView.binding.fileDate.setText(getFileDate());
            myView.binding.fileSize.setText(getFolderSizeLabel(this.selectedFile));
            if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_ZIP) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_TAR) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_7Z) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_RAR)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.zip_file));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_APK)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.apk));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_AUDIO) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_WAV) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_OGG)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_thmb_audio));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_DOC) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_DOCX)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.doc));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_XLS) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_XLSX)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.xls));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PDF) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PDF_CAPS)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.pdf));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PPT) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PPTX)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ppt));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_TXT) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_HTML) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_XML) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_RTF)) {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.txt));
            } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PICTURE_JPEG) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PICTURE) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PNG) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_MP4) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_MOV)) {
                if (!AppPref.IsThumbnail(this.mContext)) {
                    if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PICTURE) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_PNG)) {
                        myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.picture));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            myView.binding.iconView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.fab_color)));
                        }
                    } else if (this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_MP4) || this.selectedFile.getName().endsWith(MainConstant.FILE_TYPE_MOV)) {
                        myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_video));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            myView.binding.iconView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this.mContext, R.color.fab_color)));
                        }
                    }
                } else {
                    Glide.with(this.mContext).load(this.selectedFile.getPath()).apply((BaseRequestOptions<?>) new RequestOptions().override(200, 200).transform(new CenterCrop(), new RoundedCorners(20))).into(myView.binding.iconView);
                }
            } else {
                myView.binding.iconView.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.drawable.ic_file));
            }
        }
        myView.binding.fileNameTextView.setText(this.selectedFile.getName());
    }

    public static void openFile(Context context, String str, File file)  {
        try {
            File selectedFile = file;
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (uri.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf");
            } else if (uri.toString().contains(".doc") || uri.toString().contains(".docs")) {
                intent.setDataAndType(uri, "application/msword");
            } else if (uri.toString().contains(".odt")) {
                intent.setDataAndType(uri, "application/vnd.oasis.opendocument.text");
            } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx") ||
                    uri.toString().contains(".ods")) {
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (uri.toString().contains(".txt")) {
                intent.setDataAndType(uri, "text/plain");
            } else if (uri.toString().contains(".odt")) {
                intent.setDataAndType(uri, "application/vnd.oasis.opendocument.text");
            } else if (uri.toString().contains(".html")) {
                intent.setDataAndType(uri, "text/html");
            } else if (uri.toString().contains(".rar")) {
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (uri.toString().contains(".tar")) {
                intent.setDataAndType(uri, "application/tar+gzip");
            } else if (uri.toString().contains(".zip")) {
                intent.setDataAndType(uri, "application/zip");
            } else if (uri.toString().contains(".epub") ||
                    uri.toString().contains(".cbz") ||
                    uri.toString().contains(".cbr") ||
                    uri.toString().contains(".f2b") ||
                    uri.toString().contains(".mobi")) {
                intent.setDataAndType(uri, "application/*+zip");
            } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3") ||
                    uri.toString().contains(".mp4a") || uri.toString().contains(".pcm") ||
                    uri.toString().contains(".aiff") || uri.toString().contains(".aac") ||
                    uri.toString().contains(".ogg") || uri.toString().contains(".wma")
                    || uri.toString().contains(".flac") || uri.toString().contains(".alac")
                    || uri.toString().contains(".wma") || uri.toString().contains(".m4a")) {
                intent.setDataAndType(uri, "audio/*");
            } else if (uri.toString().contains(".mp4") || uri.toString().contains(".mov") ||
                    uri.toString().contains(".mp4a") || uri.toString().contains(".avi") ||
                    uri.toString().contains(".flv") || uri.toString().contains(".mvk")
                    || uri.toString().contains(".wmv") || uri.toString().contains(".avchd")
                    || uri.toString().contains("webm")) {
                intent.setDataAndType(uri, "video/*");
            } else if (uri.toString().contains(".jpeg") || uri.toString().contains(".png") ||
                    uri.toString().contains(".jpg") ||
                    uri.toString().contains(".gif") || uri.toString().contains(".bpm") ||
                    uri.toString().contains(".webp")) {
                intent.setDataAndType(uri, "image/*");
            } else if (uri.toString().contains(".html")) {
                intent.setDataAndType(uri, "text/html");
            } else if (uri.toString().contains(".json")) {
                intent.setDataAndType(uri, "text/html");
            } else if (uri.toString().contains(".txt") ||
                    uri.toString().contains(".rft") ||
                    uri.toString().contains(".odt")) {
                intent.setDataAndType(uri, "text/plain");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }





























































    @Override
    public int getItemCount() {
        return this.filterList.size();
    }

    public void removeItem(ArrayList<File> arrayList) {
        this.filterList.removeAll(arrayList);
    }

    public void selectAll() {
        this.isSelectAll = true;
        notifyDataSetChanged();
    }

    public void unselectall() {
        this.isSelectAll = false;
        notifyDataSetChanged();
    }


    public class MyView extends RecyclerView.ViewHolder {
        ItemAllFileBinding binding;

        public MyView(View view) {
            super(view);
            ItemAllFileBinding itemAllFileBinding = (ItemAllFileBinding) DataBindingUtil.bind(view);
            this.binding = itemAllFileBinding;
            itemAllFileBinding.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    AllFileAdapter.MyView.this.m127x511cdea0(view2);
                }
            });
            this.binding.llCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    if (AllFileAdapter.this.selectedFileList.contains(AllFileAdapter.this.filterList.get(MyView.this.getAdapterPosition()))) {
                        AllFileAdapter.this.selectedFileList.remove(AllFileAdapter.this.filterList.get(MyView.this.getAdapterPosition()));
                        MyView.this.binding.checkBox.setChecked(false);
                        MyView.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(AllFileAdapter.this.mContext, R.color.main_bg));
                    } else {
                        AllFileAdapter.this.selectedFileList.add(AllFileAdapter.this.filterList.get(MyView.this.getAdapterPosition()));
                        MyView.this.binding.checkBox.setChecked(true);
                        MyView.this.binding.llMain.setBackgroundColor(ContextCompat.getColor(AllFileAdapter.this.mContext, R.color.selected_card));
                    }
                    AllFileAdapter.this.cheakBoxClick.onCheakBoxClick(MyView.this.getAdapterPosition(), AllFileAdapter.this.selectedFileList);
                }
            });
        }


        public void m127x511cdea0(View view) {
            if (AllFileAdapter.this.filterList.get(getAdapterPosition()).isDirectory()) {
                AllFileAdapter.this.communication.respond(AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath());
                return;
            }
            try {
                if (AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath().endsWith(AppConstants.ZIP_FORMAT) ||
                        AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath().endsWith(AppConstants._7Z_FORMAT) ||
                        AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath().endsWith(AppConstants.TAR_FORMAT) ||
                        AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath().endsWith(AppConstants.RAR_FORMAT)) {
                    Intent intent = new Intent(AllFileAdapter.this.mContext, ShowZipContentActivity.class);
                    intent.putExtra("zipFileName", AllFileAdapter.this.filterList.get(getAdapterPosition()));
                    intent.putExtra("folderName", AllFileAdapter.this.filterList.get(getAdapterPosition()).getName());
                    AllFileAdapter.this.mContext.startActivity(intent);
                }else{
                    AllFileAdapter.openFile(AllFileAdapter.this.mContext, AllFileAdapter.this.filterList.get(getAdapterPosition()).getAbsolutePath(), AllFileAdapter.this.filterList.get(getAdapterPosition()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<File> getList() {
        return this.filterList;
    }

    public void setList(ArrayList<File> arrayList) {
        this.fileList = arrayList;
        this.filterList = arrayList;
        notifyDataSetChanged();
    }

    public String getDirFileCount() {
        File[] listFiles = this.selectedFile.listFiles();
        int i = 0;
        if (listFiles != null) {
            int i2 = 0;
            while (i < listFiles.length) {
                if (listFiles[i].getName().startsWith(".")) {
                    i2 = listFiles.length - 1;
                } else {
                    i2 = listFiles.length;
                }
                i++;
            }
            i = i2;
        }
        if (i > 1) {
            return i + " items";
        }
        return i + " item";
    }

    public String getDirFileDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.selectedFile.listFiles() != null ? this.selectedFile.lastModified() : 0L));
    }

    public String getFileDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(this.selectedFile.lastModified()));
    }

    public String getFolderSizeLabel(File file) {
        long length = file.length();
        if (length < 0 || length >= FileUtils.ONE_KB) {
            if (length < FileUtils.ONE_KB || length >= FileUtils.ONE_MB) {
                if (length < FileUtils.ONE_MB || length >= FileUtils.ONE_GB) {
                    if (length < FileUtils.ONE_GB || length >= 1099511627776L) {
                        if (length >= 1099511627776L) {
                            return (length / 1099511627776L) + " TB";
                        }
                        return length + " Bytes";
                    }
                    return (length / FileUtils.ONE_GB) + " GB";
                }
                return (length / FileUtils.ONE_MB) + " MB";
            }
            return (length / FileUtils.ONE_KB) + " KB";
        }
        return length + " B";
    }

    public static long getFolderSize(File file) {
        if (file.isDirectory()) {
            long j = 0;
            for (File file2 : file.listFiles()) {
                j += getFolderSize(file2);
            }
            return j;
        }
        return file.length();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                String trim = charSequence.toString().trim();
                Filter.FilterResults filterResults = new Filter.FilterResults();
                Log.e("con", "performFiltering: " + ((Object) trim));
                ArrayList arrayList = new ArrayList();
                if (trim.length() > 0) {
                    AllFileAdapter.this.isFilter = true;
                    Iterator<File> it = AllFileAdapter.this.fileList.iterator();
                    while (it.hasNext()) {
                        File next = it.next();
                        if (next.getName().toLowerCase().contains(trim.toString().toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                } else {
                    filterResults.values = AllFileAdapter.this.fileList;
                    filterResults.count = AllFileAdapter.this.fileList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                AllFileAdapter.this.filterList = (ArrayList) filterResults.values;
                AllFileAdapter.this.notifyDataSetChanged();
            }
        };
    }

    public void removeItem(int i) {
        this.selectedFileList.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, this.selectedFileList.size());
    }
}
