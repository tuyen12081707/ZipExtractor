package com.demo.zipextractor.utils;

import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.util.InternalZipConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class ZipManager {
    private static int BUFFER = 2048;

    public static void zip(String[] strArr, String str) throws IOException {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(str)));
            byte[] bArr = new byte[BUFFER];
            for (String s : strArr) {
                Log.d("add:", s);
                Log.v("Compress", "Adding: " + s);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(s), BUFFER);
                zipOutputStream.putNextEntry(new ZipEntry(FilenameUtils.getBaseName(s)));
                while (true) {
                    int read = bufferedInputStream.read(bArr, 0, BUFFER);
                    if (read != -1) {
                        zipOutputStream.write(bArr, 0, read);
                    }
                }

            }
            zipOutputStream.setLevel(0);
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zipSpeed(List<File> list, String str) throws IOException {
        new ZipFile(str).addFiles(list);
    }

    public static void zipSingle(File file, String str) throws IOException {
        new ZipFile(str).addFile(file);
    }

    public static void unzip(String str, String str2) throws IOException {
        new ZipFile(str).extractAll(str2);
    }

    public static void unzipSpeed(String str, String str2) throws IOException {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(str));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    Log.v("Decompress", "Unzipping " + nextEntry.getName());
                    if (nextEntry.isDirectory()) {
                        _dirChecker(nextEntry.getName(), str2);
                    } else {
                        FileOutputStream fileOutputStream = new FileOutputStream(str2 + nextEntry.getName());
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        byte[] bArr = new byte[1024];
                        for (int read = bufferedInputStream.read(bArr, 0, 1024); read != -1; read = bufferedInputStream.read()) {
                            bufferedOutputStream.write(bArr, 0, read);
                        }
                        zipInputStream.closeEntry();
                        bufferedOutputStream.close();
                        fileOutputStream.close();
                    }
                } else {
                    zipInputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
        }
    }

    private static void _dirChecker(String str, String str2) {
        File file = new File(str2 + str);
        if (file.isDirectory()) {
            return;
        }
        file.mkdirs();
    }

    public static File zipFolder(File file, String str, String str2) {
        File file2 = new File(file.getParent(), String.format("%s" + str, str2));
        Log.e("ContentValues", "zipFolder: name " + file.getName());
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file2));
            zipSubFolder(zipOutputStream, file, file.getPath().length());
            zipOutputStream.close();
            return file2;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void zipSubFolder(ZipOutputStream zipOutputStream, File file, int i) throws IOException {
        File[] listFiles;
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                zipSubFolder(zipOutputStream, file2, i);
            } else {
                byte[] bArr = new byte[2048];
                String path = file2.getPath();
                String substring = path.substring(i + 1);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path), 2048);
                ZipEntry zipEntry = new ZipEntry(substring);
                zipEntry.setTime(file2.lastModified());
                zipOutputStream.putNextEntry(zipEntry);
                while (true) {
                    int read = bufferedInputStream.read(bArr, 0, 2048);
                    if (read == -1) {
                        break;
                    }
                    zipOutputStream.write(bArr, 0, read);
                }
                bufferedInputStream.close();
                zipOutputStream.closeEntry();
            }
        }
    }

    public static void zipFolders(String str, String str2, String str3, String str4) throws ZipException {
        new ZipFile(str2 + str4).addFolder(new File(str));
    }

    public static void encryptZip(List<File> list, CompressionLevel compressionLevel, String str, String str2) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        zipParameters.setCompressionLevel(compressionLevel);
        ZipFile zipFile = new ZipFile(str2, str.toCharArray());
        zipFile.addFiles(list, zipParameters);
        zipFile.close();
        Log.e("ContentValues", "encryptZip: success");
    }

    public static void encryptZipFolder(File file, CompressionLevel compressionLevel, String str, String str2) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        zipParameters.setCompressionLevel(compressionLevel);
        ZipFile zipFile = new ZipFile(str2, str.toCharArray());
        zipFile.addFolder(file, zipParameters);
        zipFile.close();
        Log.e("ContentValues", "encryptZip: success");
    }

    public static void zipFileAndFolder(List<File> list, CompressionLevel compressionLevel, String str, String str2, boolean z) {
        if (!z) {
            ZipParameters zipParameters = new ZipParameters();
            ZipFile zipFile = new ZipFile(str);
            if (!str2.isEmpty()) {
                zipFile = new ZipFile(str, str2.toCharArray());
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            }
            zipParameters.setCompressionLevel(compressionLevel);
            for (File file : list) {
                try {
                    if (file.isDirectory()) {
                        if (!str2.isEmpty()) {
                            zipFile.addFolder(file, zipParameters);
                        } else {
                            zipFile.addFolder(file);
                        }
                    } else if (!str2.isEmpty()) {
                        zipFile.addFile(file, zipParameters);
                    } else {
                        zipFile.addFile(file);
                    }
                } catch (ZipException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        Log.e("ContentValues", "zipFileAndFolder: " + z);
    }

    public static HashMap<String, List<String>> retrieveListing(File file) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                if (nextEntry.isDirectory() && !nextEntry.getName().contains(".")) {
                    File file2 = new File(nextEntry.getName());
                    String name = nextEntry.getName();
                    if (!hashMap.containsKey(name)) {
                        hashMap.put(name, new ArrayList());
                        if (Build.VERSION.SDK_INT >= 24) {
                            Log.e("ContentValues", "extracting dir .................. : " + file2.getName());
                        }
                    }
                } else {
                    String name2 = nextEntry.getName();
                    int lastIndexOf = name2.lastIndexOf(InternalZipConstants.ZIP_FILE_SEPARATOR);
                    if (lastIndexOf != -1) {
                        int i = lastIndexOf + 1;
                        String substring = name2.substring(0, i);
                        String substring2 = name2.substring(i);
                        if (!hashMap.containsKey(substring)) {
                            hashMap.put(substring, new ArrayList());
                            hashMap.get(substring).add(substring2);
                            final ArrayList arrayList = new ArrayList();
                            if (Build.VERSION.SDK_INT >= 24) {
                                hashMap.forEach(new BiConsumer() {
                                    @Override
                                    public final void accept(Object obj, Object obj2) {
                                        ZipManager.lambda$retrieveListing$0(arrayList, (String) obj, (List) obj2);
                                    }
                                });
                                Log.e("ContentValues", "extracting folder.................. : " + substring2);
                            }
                        } else {
                            hashMap.get(substring).add(substring2);
                            final ArrayList arrayList2 = new ArrayList();
                            if (Build.VERSION.SDK_INT >= 24) {
                                hashMap.forEach(new BiConsumer() {
                                    @Override
                                    public final void accept(Object obj, Object obj2) {
                                        ZipManager.lambda$retrieveListing$1(arrayList2, (String) obj, (List) obj2);
                                    }
                                });
                                Log.e("ContentValues", "extracting else folder.................. : " + substring2);
                            }
                        }
                        Log.e("ContentValues", "retrieveListing: " + name2);
                    } else {
                        if (!hashMap.containsKey("root")) {
                            hashMap.put("root", new ArrayList());
                        }
                        hashMap.get("root").add(name2);
                        final ArrayList arrayList3 = new ArrayList();
                        if (Build.VERSION.SDK_INT >= 24) {
                            hashMap.forEach(new BiConsumer() {
                                @Override
                                public final void accept(Object obj, Object obj2) {
                                    ZipManager.lambda$retrieveListing$2(arrayList3, (String) obj, (List) obj2);
                                }
                            });
                            Log.e("ContentValues", "extracting.................. : " + ((String) arrayList3.get(0)));
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static void lambda$retrieveListing$0(ArrayList arrayList, String str, List list) {
        arrayList.addAll(list);
    }

    public static void lambda$retrieveListing$1(ArrayList arrayList, String str, List list) {
        arrayList.addAll(list);
    }

    public static void lambda$retrieveListing$2(ArrayList arrayList, String str, List list) {
        arrayList.addAll(list);
    }

    public static void renameFile(String str, String str2, String str3) {
        if (new File("/storage/emulated/0/pot.zip").renameTo(new File("/storage/emulated/0/pot11.zip"))) {
            System.out.println("file is renamed..");
        }
    }

    public static void copy(String str, String str2) throws IOException {
        File file = new File(str);
        File file2 = new File(str2);
        try {
            if (!file.isDirectory()) {
                FileUtils.copyFileToDirectory(file, file2);
            } else {
                FileUtils.copyDirectoryToDirectory(file, file2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void move(String str, String str2) throws IOException {
        File file = new File(str);
        File file2 = new File(str2);
        if (!file.isDirectory()) {
            try {
                FileUtils.copyFileToDirectory(file, file2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.copyDirectoryToDirectory(file, file2);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        AppConstants.doDelete(file);
    }

    public static void doDelete(File file) throws IOException {
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        } else {
            file.delete();
        }
    }
}
