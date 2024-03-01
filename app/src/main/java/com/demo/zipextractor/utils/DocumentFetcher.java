package com.demo.zipextractor.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.demo.zipextractor.model.FileListModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.lingala.zip4j.util.InternalZipConstants;


public class DocumentFetcher {
    public static int ORDER_AZ = 1;


    public interface OnFileFetchListnear {
        void onFileFetched(List<FileListModel> list);
    }

    public DocumentFetcher(final Context context, int i, final int i2, final String str, int i3, final OnFileFetchListnear onFileFetchListnear) {
        LoaderManager.getInstance((AppCompatActivity) context).initLoader(i, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            private String sortOrder;

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }

            @Override
            public Loader<Cursor> onCreateLoader(int i4, Bundle bundle) {
                Uri contentUri;
                String[] strArr = {"_display_name", "date_added", "_data", "mime_type", "_size", "title"};
                this.sortOrder = "date_added DESC";
                String str2 = (!str.isEmpty() ? Arrays.toString(new String[]{"%" + (Environment.DIRECTORY_DOCUMENTS + "/Docx") + "%"}) : "") + DocumentFetcher.this.getSelectionFromType(i2);
                if (Build.VERSION.SDK_INT >= 29) {
                    contentUri = MediaStore.Files.getContentUri("external");
                } else {
                    contentUri = MediaStore.Files.getContentUri("external");
                }
                return new CursorLoader(context, contentUri, strArr, str2, null, this.sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor != null) {
                    try {
                        if (cursor.isClosed()) {
                            return;
                        }
                        Log.d("TAG", "onLoadFinished: " + cursor.getCount());
                        ArrayList arrayList = new ArrayList();
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex("_data");
                            int columnIndex2 = cursor.getColumnIndex("date_added");
                            cursor.getColumnIndex("title");
                            int columnIndex3 = cursor.getColumnIndex("_size");
                            int columnIndex4 = cursor.getColumnIndex("mime_type");
                            do {
                                FileListModel fileListModel = new FileListModel();
                                fileListModel.setFilename(cursor.getString(columnIndex).substring(cursor.getString(columnIndex).lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR) + 1));
                                fileListModel.setFilePath(cursor.getString(columnIndex));
                                if (!TextUtils.isEmpty(cursor.getString(columnIndex3)) && !TextUtils.isEmpty(cursor.getString(columnIndex2))) {
                                    fileListModel.setFileDate(Long.parseLong(cursor.getString(columnIndex2)) * 1000);
                                    fileListModel.setFileSize(Long.parseLong(cursor.getString(columnIndex3)));
                                    fileListModel.setFileType(cursor.getString(columnIndex4));
                                    if (i2 == FileRoot.ZIP) {
                                        if (fileListModel.getFileSize() > 0 && (fileListModel.getFilename().endsWith(MainConstant.FILE_TYPE_7Z) || fileListModel.getFilename().endsWith(MainConstant.FILE_TYPE_ZIP) || fileListModel.getFilename().endsWith(MainConstant.FILE_TYPE_RAR) || fileListModel.getFilename().endsWith(MainConstant.FILE_TYPE_TAR) || fileListModel.getFilename().endsWith("gz"))) {
                                            arrayList.add(fileListModel);
                                        }
                                    } else if (fileListModel.getFileSize() > 0) {
                                        arrayList.add(fileListModel);
                                    }
                                }
                            } while (cursor.moveToNext());
                            cursor.close();
                            onFileFetchListnear.onFileFetched(arrayList);
                        }
                        cursor.close();
                        onFileFetchListnear.onFileFetched(arrayList);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getSelectionFromType(int i) {
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PDF);
        String mimeTypeFromExtension2 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PDF_CAPS);
        String mimeTypeFromExtension3 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_DOC);
        String mimeTypeFromExtension4 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_DOCX);
        String mimeTypeFromExtension5 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PPT);
        String mimeTypeFromExtension6 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PPTX);
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_ZIP);
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_TAR);
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_7Z);
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_RAR);
        String mimeTypeFromExtension7 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_APK);
        String mimeTypeFromExtension8 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PICTURE);
        String mimeTypeFromExtension9 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PICTURE_JPEG);
        String mimeTypeFromExtension10 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_PNG);
        String mimeTypeFromExtension11 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_TIFF);
        String mimeTypeFromExtension12 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_EPS);
        String mimeTypeFromExtension13 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_BITMAP);
        String mimeTypeFromExtension14 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_GIF);
        String mimeTypeFromExtension15 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_TIF);
        String mimeTypeFromExtension16 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_MP4);
        String mimeTypeFromExtension17 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_MOV);
        String mimeTypeFromExtension18 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_MKV);
        String mimeTypeFromExtension19 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_WMV);
        String mimeTypeFromExtension20 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_M2TS);
        String mimeTypeFromExtension21 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_FLV);
        String mimeTypeFromExtension22 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_F4V);
        String mimeTypeFromExtension23 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_SWF);
        String mimeTypeFromExtension24 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_AVI);
        String mimeTypeFromExtension25 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_AUDIO);
        String mimeTypeFromExtension26 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_WAV);
        String mimeTypeFromExtension27 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_OGG);
        String mimeTypeFromExtension28 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_AIFF);
        String mimeTypeFromExtension29 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_FLC);
        String mimeTypeFromExtension30 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_ALAC);
        String mimeTypeFromExtension31 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_WMA);
        String mimeTypeFromExtension32 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_AAC);
        String mimeTypeFromExtension33 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_XLS);
        String mimeTypeFromExtension34 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_XLSX);
        String mimeTypeFromExtension35 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_TXT);
        String mimeTypeFromExtension36 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_HTML);
        String mimeTypeFromExtension37 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_XML);
        String mimeTypeFromExtension38 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MainConstant.FILE_TYPE_RTF);
        if (i == FileRoot.ALL_DOC) {
            return "mime_type LIKE '" + mimeTypeFromExtension3 + "%' OR mime_type LIKE '" + mimeTypeFromExtension4 + "%' OR mime_type LIKE '" + mimeTypeFromExtension33 + "%' OR mime_type LIKE '" + mimeTypeFromExtension34 + "%' OR mime_type LIKE '" + mimeTypeFromExtension + "%' OR mime_type LIKE '" + mimeTypeFromExtension5 + "%' OR mime_type LIKE '" + mimeTypeFromExtension6 + "%' OR mime_type LIKE '" + mimeTypeFromExtension35 + "%' OR mime_type LIKE '" + mimeTypeFromExtension36 + "%' OR mime_type LIKE '" + mimeTypeFromExtension37 + "%' OR mime_type LIKE '" + mimeTypeFromExtension38 + "%' ";
        }
        if (i == FileRoot.PDF) {
            return "mime_type LIKE '" + mimeTypeFromExtension + "%' OR mime_type LIKE '" + mimeTypeFromExtension2 + "%' ";
        }
        if (i == FileRoot.PPT) {
            return "mime_type LIKE '" + mimeTypeFromExtension5 + "%' OR mime_type LIKE '" + mimeTypeFromExtension6 + "%' ";
        }
        if (i == FileRoot.WORD) {
            return "mime_type LIKE '" + mimeTypeFromExtension3 + "%' OR mime_type LIKE '" + mimeTypeFromExtension4 + "%' ";
        }
        if (i == FileRoot.ZIP) {
            return "";
        }
        if (i == FileRoot.APK) {
            return "mime_type LIKE '" + mimeTypeFromExtension7 + "%' ";
        }
        if (i == FileRoot.PICTURE) {
            return "mime_type LIKE '" + mimeTypeFromExtension8 + "%' OR mime_type LIKE '" + mimeTypeFromExtension10 + "%' OR mime_type LIKE '" + mimeTypeFromExtension11 + "%' OR mime_type LIKE '" + mimeTypeFromExtension12 + "%' OR mime_type LIKE '" + mimeTypeFromExtension13 + "%' OR mime_type LIKE '" + mimeTypeFromExtension14 + "%' OR mime_type LIKE '" + mimeTypeFromExtension15 + "%' OR mime_type LIKE '" + mimeTypeFromExtension9 + "%' ";
        }
        if (i == FileRoot.VIDEO) {
            return "mime_type LIKE '" + mimeTypeFromExtension16 + "%' OR mime_type LIKE '" + mimeTypeFromExtension17 + "%' OR mime_type LIKE '" + mimeTypeFromExtension18 + "%' OR mime_type LIKE '" + mimeTypeFromExtension19 + "%' OR mime_type LIKE '" + mimeTypeFromExtension20 + "%' OR mime_type LIKE '" + mimeTypeFromExtension21 + "%' OR mime_type LIKE '" + mimeTypeFromExtension22 + "%' OR mime_type LIKE '" + mimeTypeFromExtension23 + "%' OR mime_type LIKE '" + mimeTypeFromExtension24 + "%' ";
        }
        if (i == FileRoot.AUDIO) {
            return "mime_type LIKE '" + mimeTypeFromExtension25 + "%' OR mime_type LIKE '" + mimeTypeFromExtension26 + "%' OR mime_type LIKE '" + mimeTypeFromExtension27 + "%' OR mime_type LIKE '" + mimeTypeFromExtension28 + "%' OR mime_type LIKE '" + mimeTypeFromExtension29 + "%' OR mime_type LIKE '" + mimeTypeFromExtension30 + "%' OR mime_type LIKE '" + mimeTypeFromExtension31 + "%' OR mime_type LIKE '" + mimeTypeFromExtension32 + "%' ";
        }
        if (i == FileRoot.EXCEL) {
            return "mime_type LIKE '" + mimeTypeFromExtension33 + "%' OR mime_type LIKE '" + mimeTypeFromExtension34 + "%' ";
        }
        if (i == FileRoot.TXT) {
            return "mime_type LIKE '" + mimeTypeFromExtension35 + "%' OR mime_type LIKE '" + mimeTypeFromExtension36 + "%' OR mime_type LIKE '" + mimeTypeFromExtension37 + "%' OR mime_type LIKE '" + mimeTypeFromExtension38 + "%' ";
        }
        return "mime_type LIKE '" + mimeTypeFromExtension3 + "%' OR mime_type LIKE '" + mimeTypeFromExtension4 + "%' OR mime_type LIKE '" + mimeTypeFromExtension5 + "%' OR mime_type LIKE '" + mimeTypeFromExtension6 + "%' ";
    }
}
