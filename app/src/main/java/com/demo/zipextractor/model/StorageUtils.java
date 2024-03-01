package com.demo.zipextractor.model;

import android.content.Context;
import android.os.StatFs;


public class StorageUtils {
    public static SDCardInfoModel getSystemSpaceinfo(Context context, String str) {
        StatFs statFs = new StatFs(str);
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong() * blockSizeLong;
        long availableBlocksLong = statFs.getAvailableBlocksLong() * blockSizeLong;
        SDCardInfoModel sDCardInfoModel = new SDCardInfoModel();
        sDCardInfoModel.total = blockCountLong;
        sDCardInfoModel.free = availableBlocksLong;
        return sDCardInfoModel;
    }
}
