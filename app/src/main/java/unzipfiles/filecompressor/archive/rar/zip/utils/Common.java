package unzipfiles.filecompressor.archive.rar.zip.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import unzipfiles.filecompressor.archive.rar.zip.R;
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel;

public class Common {

    public static boolean isDelete = false;

    public static String INTENT_PROCESS = "intent_process";
    public static String INTENT_EXTRACT = "intent_extract";
    public static String INTENT_COMPRESS = "intent_compress";

    public static String getTypeCompress = null;
    public static String getFolderCompress = null;
    public static String getNameCompress = null;
    public static String getPassCompress = null;

    public static String getFolderExtract = null;
    public static String getPassExtract = null;

    public static List<File> getListFile = null;

    public static void setFirstOpen(boolean first, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("firstOpen", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstOpen", first).apply();
    }

    public static boolean isFirstOpen(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("firstOpen", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isFirstOpen", true);
    }

    public static ArrayList<SelectLanguageModel> getListLocation(Context context) {
        ArrayList<SelectLanguageModel> listLanguage = new ArrayList<>();
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_england_flag, context.getString(R.string.english), "en", true));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_india_flag, context.getString(R.string.hindi), "hi", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_spanish_flag, context.getString(R.string.spanish), "es", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_french_flag, context.getString(R.string.french), "fr", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_arabic_flag, context.getString(R.string.arabic), "ar", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_bengal_flag, context.getString(R.string.bengali), "bn", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_russian_flag, context.getString(R.string.russian), "ru", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_portugal, context.getString(R.string.portuguese), "pt", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_indo_flag, context.getString(R.string.indonesian), "in", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_german_flag, context.getString(R.string.german), "de", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_itali_flag, context.getString(R.string.italian), "it", false));
        listLanguage.add(new SelectLanguageModel(R.drawable.ic_korean_flag, context.getString(R.string.korean), "ko", false));
        return listLanguage;
    }

    public static void setLocationPosition(Context context, int position){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("position", position);
        editor.apply();
    }

    public static int getLocationPosition(Context context){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
        return shared.getInt("position", 0);
    }
}
