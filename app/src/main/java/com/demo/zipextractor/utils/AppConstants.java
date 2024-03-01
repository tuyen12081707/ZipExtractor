package com.demo.zipextractor.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.demo.zipextractor.model.FileListModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class AppConstants {
    public static String DISCLOSURE_DIALOG_DESC = "We would like to inform you regarding the 'Consent to Collection and Use Of PostFeed'\n\nTo Export Excel file into your directory, allow storage permission.\n\nWe store your data on your device only, we don’t store them on our server.";

    public static String FASTEST_LEVEL = "Fastest";
    public static String FAST_LEVEL = "Fast";
    public static String MAXIMUM_LEVEL = "Maximum";
    public static String PRIVACY_POLICY_URL = "https://google.com";
    public static String RAR_FORMAT = ".rar";
    public static String ROOT_PATH = "/storage/emulated";
    public static String ROOT_PATH1 = "/storage/emulated/0/";
    public static String STORE_LEVEL = "Store";
    public static String TAR_FORMAT = ".tar";
    public static String TERMS_OF_SERVICE_URL = "https://google.com";
    public static String ULTRA_LEVEL = "Ultra";
    public static String ZIP_FORMAT = ".zip";
    public static String _7Z_FORMAT = ".7z";
    public static String title = "Support us by giving rate and your precious review !!\nIt will take few seconds only.";
    public static final String[] fileFormatList = {".zip", ".7z", ".tar", ".rar"};
    public static final String[] compressLevelList = {"Normal", "Fast", "Fastest", "Maximum", "Ultra", "Store"};

    public static void doDelete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                doDelete(file2);
            }
        } else if (!file.delete()) {
            throw new IOException("Failed to delete " + file + '!');
        }
    }

    public static <T> List<T> convertArrayToList(T[] tArr) {
        ArrayList arrayList = new ArrayList();
        if (tArr != null) {
            for (T t : tArr) {
                arrayList.add(t);
            }
        }
        return arrayList;
    }

    private String[] getAllVideoPath(Context context) {
        Cursor query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, null, null, null);
        ArrayList arrayList = new ArrayList();
        if (query != null) {
            while (query.moveToNext()) {
                arrayList.add(query.getString(0));
            }
            query.close();
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public static void recursiveList(String str, String str2, ArrayList<FileListModel> arrayList) {
        File[] listFiles = new File(str).listFiles();
        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory() && !listFiles[i].isHidden() && listFiles[i].length() > 0) {
                    recursiveList(listFiles[i].getAbsolutePath(), str2, arrayList);
                } else if (listFiles[i].getPath().endsWith(str2) && listFiles[i].length() > 0) {
                    arrayList.add(new FileListModel(listFiles[i].getPath(), listFiles[i].getName(), listFiles[i].length(), listFiles[i].lastModified(), str2, ""));
                }
            }
        }
    }

    public static ArrayList<FileListModel> getAllFilesExtension(String str) {
        File[] listFiles;
        File[] listFiles2;
        ArrayList<FileListModel> arrayList = new ArrayList<>();
        for (File file : new File(ROOT_PATH1).listFiles()) {
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    if (FilenameUtils.getExtension(file2.getName()).endsWith(str)) {
                        arrayList.add(new FileListModel(file2.getPath(), file2.getName(), file2.length(), file2.lastModified(), "", ""));
                    }
                }
            } else if (FilenameUtils.getExtension(file.getName()).endsWith(str)) {
                arrayList.add(new FileListModel(file.getPath(), file.getName(), file.length(), file.lastModified(), "", ""));
            }
        }
        return arrayList;
    }

    public static ArrayList<FileListModel> getFileListByExtension(Activity activity, String str) {
        Uri contentUri;
        ArrayList<FileListModel> arrayList = new ArrayList<>();
        String[] strArr = {"_display_name", "date_modified", "_data", "_size", "mime_type", "date_added"};
        String[] strArr2 = {MimeTypeMap.getSingleton().getMimeTypeFromExtension(str)};
        if (Build.VERSION.SDK_INT >= 29) {
            contentUri = MediaStore.Files.getContentUri("external");
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        Cursor query = activity.getContentResolver().query(contentUri, strArr, "mime_type = ?", strArr2, null);
        try {
            if (query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("_data");
                int columnIndex2 = query.getColumnIndex("_display_name");
                int columnIndex3 = query.getColumnIndex("_size");
                int columnIndex4 = query.getColumnIndex("date_modified");
                int columnIndex5 = query.getColumnIndex("mime_type");
                while (true) {
                    int i = columnIndex3;
                    arrayList.add(new FileListModel(query.getString(columnIndex), query.getString(columnIndex2), query.getLong(columnIndex3), query.getLong(columnIndex4) * 1000, query.getString(columnIndex5), ""));
                    Log.d("ContentValues", "date : " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(query.getLong(columnIndex4) * 1000)));
                    Log.d("ContentValues", "name : " + query.getString(columnIndex2));
                    Log.d("ContentValues", "size : " + query.getString(columnIndex));
                    if (!query.moveToNext()) {
                        break;
                    }
                    columnIndex3 = i;
                }
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }

    public static void ascending(List<File> list) {
        Collections.sort(list);
    }

    public static void desending(List<File> list) {
        Collections.sort(list, Collections.reverseOrder());
    }

    public static ArrayList<File> getAllFile(String str) {
        ArrayList<File> arrayList = new ArrayList<>();
        try {
            arrayList.addAll(Arrays.asList(new File(str).listFiles()));
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean checkStoragePermissionApi30(Context context) {
        int unsafeCheckOpNoThrow = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                unsafeCheckOpNoThrow = ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE)).unsafeCheckOpNoThrow(AppOpsManager.permissionToOp("android.permission.MANAGE_EXTERNAL_STORAGE"), context.getApplicationInfo().uid, context.getApplicationInfo().packageName);
            }
        }
        if (unsafeCheckOpNoThrow == 3) {
            if (context.checkCallingOrSelfPermission("android.permission.MANAGE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        } else if (unsafeCheckOpNoThrow == 0) {
            return true;
        }
        return false;
    }

    public static boolean checkStoragePermissionApi19(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    public static ArrayList<File> getAllFileExtension(String str) {
        Collection listFiles = FileUtils.listFiles(new File(ROOT_PATH + "/0/"), new String[]{str}, true);
        ArrayList<File> arrayList = new ArrayList<>(listFiles);
        for (int i = 0; i < listFiles.size(); i++) {
            Log.e("ContentValues", "getAllFileExtension: " + arrayList.get(i).getName());
        }
        return arrayList;
    }

    public static void openWith(Activity activity, Uri uri, String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(uri, str);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(activity, "No suitable app found", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getCount(String str) {
        File[] listFiles = new File(str).listFiles();
        if (listFiles != null) {
            int i = 0;
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    file.getName();
                    i++;
                    System.out.println("170 " + i);
                }
            }
            return;
        }
        System.out.println("170 1");
    }

    public static void share(Context context, List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        ArrayList<Uri> arrayList = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("*/*");
        for (String str : list) {
            arrayList.add(FileProvider.getUriForFile(context, context.getPackageName()+".provider", new File(str)));
        }
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        context.startActivity(intent);
    }

    public static boolean cheakExits(String str, String str2) {
        for (File file : new File(str).listFiles()) {
            if (FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase(str2.trim())) {
                return true;
            }
        }
        return false;
    }

    public static int lambda$sortAsc$0(FileListModel fileListModel, FileListModel fileListModel2) {
        return fileListModel.getFilename().compareToIgnoreCase(fileListModel2.getFilename());
    }

    public static void sortAsc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortAsc$0((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortDesc$1(FileListModel fileListModel, FileListModel fileListModel2) {
        return fileListModel2.getFilename().compareToIgnoreCase(fileListModel.getFilename());
    }

    public static void sortDesc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortDesc$1((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortDateAsc$2(FileListModel fileListModel, FileListModel fileListModel2) {
        return Long.compare(fileListModel.getFileDate(), fileListModel2.getFileDate());
    }

    public static void sortDateAsc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortDateAsc$2((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortDateDesc$3(FileListModel fileListModel, FileListModel fileListModel2) {
        return Long.compare(fileListModel2.getFileDate(), fileListModel.getFileDate());
    }

    public static void sortDateDesc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortDateDesc$3((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortSizeAsc$4(FileListModel fileListModel, FileListModel fileListModel2) {
        return Long.compare(fileListModel.getFileSize(), fileListModel2.getFileSize());
    }

    public static void sortSizeAsc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortSizeAsc$4((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortSizeDesc$5(FileListModel fileListModel, FileListModel fileListModel2) {
        return Long.compare(fileListModel2.getFileSize(), fileListModel.getFileSize());
    }

    public static void sortSizeDesc(ArrayList<FileListModel> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortSizeDesc$5((FileListModel) obj, (FileListModel) obj2);
            }
        });
    }

    public static int lambda$sortMainAsc$6(File file, File file2) {
        return file.getName().compareToIgnoreCase(file2.getName());
    }

    public static void sortMainAsc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortMainAsc$6((File) obj, (File) obj2);
            }
        });
    }

    public static int lambda$sortMainDesc$7(File file, File file2) {
        return file2.getName().compareToIgnoreCase(file.getName());
    }

    public static void sortMainDesc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortMainDesc$7((File) obj, (File) obj2);
            }
        });
    }

    public static int lambda$sortDateMainAsc$8(File file, File file2) {
        return Long.compare(file.lastModified(), file2.lastModified());
    }

    public static void sortDateMainAsc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortDateMainAsc$8((File) obj, (File) obj2);
            }
        });
    }

    public static int lambda$sortDateMainDesc$9(File file, File file2) {
        return Long.compare(file2.lastModified(), file.lastModified());
    }

    public static void sortDateMainDesc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortDateMainDesc$9((File) obj, (File) obj2);
            }
        });
    }

    public static int lambda$sortSizeMainAsc$10(File file, File file2) {
        return Long.compare(file.length(), file2.length());
    }

    public static void sortSizeMainAsc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortSizeMainAsc$10((File) obj, (File) obj2);
            }
        });
    }

    public static int lambda$sortSizeMainDesc$11(File file, File file2) {
        return Long.compare(file2.length(), file.length());
    }

    public static void sortSizeMainDesc(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                return AppConstants.lambda$sortSizeMainDesc$11((File) obj, (File) obj2);
            }
        });
    }

    public static void cleaner(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        cleaner(file);
        if (file.listFiles() == null || file.listFiles().length != 0) {
            return;
        }
        file.delete();
    }

    public static void refreshGallery(String str, Context context) {
        File file = new File(str);
        try {
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            Uri fromFile = Uri.fromFile(file);
            Log.i("refreshGallery", "refreshGallery: " + fromFile);
            intent.setData(fromFile);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertStorage(long j) {
        if (j >= FileUtils.ONE_GB) {
            return String.format("%.1f GB", Float.valueOf(((float) j) / ((float) FileUtils.ONE_GB)));
        }
        if (j >= FileUtils.ONE_MB) {
            float f = ((float) j) / ((float) FileUtils.ONE_MB);
            return String.format(f > 100.0f ? "%.0f MB" : "%.1f MB", Float.valueOf(f));
        } else if (j >= FileUtils.ONE_KB) {
            float f2 = ((float) j) / ((float) FileUtils.ONE_KB);
            return String.format(f2 > 100.0f ? "%.0f KB" : "%.1f KB", Float.valueOf(f2));
        } else {
            return String.format("%d B", Long.valueOf(j));
        }
    }

    @SuppressLint("WrongConstant")
    public static void openUrl(Context context, String str) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str));
        intent.addFlags(1208483840);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context, "No suitable app found", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getVersion(Context context) {
        String str;
        try {
            str = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            str = "";
        }
        return "Version " + str;
    }

    public static void shareapp(Activity activity) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", "Zip Unzip Files, Zip Extractor");
            intent.putExtra("android.intent.extra.TEXT", "Zip Unzip Files, Zip Extractor\n\nZip and Unzip your files easily with all formats, and Manage media as well\n- Manage Zip, Documents, APKs, Pictures, Video, Audio files\n\n- Select multiple Files & Folders and Create Zip Files\n- Secure Zip files with protected password\n- Extract Your Zip files at any Location\n\nhttps://play.google.com/store/apps/details?id=" + activity.getPackageName());
            activity.startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            Log.d("", e.toString());
        }
    }


    public static String formatFileSize(long j) {
        double d = j;
        double d2 = d / 1024.0d;
        double d3 = d2 / 1024.0d;
        double d4 = d3 / 1024.0d;
        double d5 = d4 / 1024.0d;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if (d5 > 1.0d) {
            return decimalFormat.format(d5).concat(" TB");
        }
        if (d4 > 1.0d) {
            return decimalFormat.format(d4).concat(" GB");
        }
        if (d3 > 1.0d) {
            return decimalFormat.format(d3).concat(" MB");
        }
        if (d2 > 1.0d) {
            return decimalFormat.format(d2).concat(" KB");
        }
        return decimalFormat.format(d).concat(" Bytes");
    }
}
