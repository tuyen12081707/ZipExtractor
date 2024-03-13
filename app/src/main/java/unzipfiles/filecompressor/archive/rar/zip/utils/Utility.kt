package unzipfiles.filecompressor.archive.rar.zip.utils

import android.app.Activity
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import org.apache.commons.io.FileUtils
import unzipfiles.filecompressor.archive.rar.zip.BuildConfig
import unzipfiles.filecompressor.archive.rar.zip.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat


fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

fun Context.sharedPrefs(): SharedPreferences {
    return getSharedPreferences("roku_shared_prefs", MODE_PRIVATE)
}

fun log(msg: String, isError: Boolean = false) {
    if (!BuildConfig.DEBUG) return
    if (isError) Log.e("===", msg) else Log.d("===", msg)
}

@JvmName("log1")
fun String.log(isError: Boolean = false) {
    if (!BuildConfig.DEBUG) return
    if (isError) Log.e("===", this) else Log.d("===", this)
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.toHttp(): String {
    if (startsWith("https")) return replace("https", "http")
    if (startsWith("http")) return this
    return "http://$this"
}

fun String.toUrl(): String {
    return if (!startsWith("http")) "http://$this"
    else this
}

fun String.isUrl() = URLUtil.isValidUrl(this)

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Context.replaceActivity(activity: Class<*>?) {
    val i = Intent(this, activity)
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(i)
}

fun Context.addActivity(activity: Class<*>?) {
    val i = Intent(this, activity)
    startActivity(i)
}

fun Context.hasPermission(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * yyyy:MM:dd hh:mm:ss:SSS a z
 * 2022:12:12 12:12:12:122 AM GMT+07:00
 */
fun Long.convertTime(pattern: String): String {
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(this)
}

/**
 * Created by Jemo on 12/5/16.
 */

fun convertDpToPix(dp: Int, context: Context): Int {
    val metric = context.resources.displayMetrics
    return (dp * (metric.densityDpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun Context.share(text: String) {
    val i = Intent(Intent.ACTION_SEND)
    i.type = "text/plain"
    i.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(i, "Share via"))
}

fun Context.sendFeedback() {
    val emailIntent = Intent(Intent.ACTION_SENDTO)
    emailIntent.data = Uri.parse("mailto:")
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("3xiappsp@gmail.com"))
    emailIntent.putExtra(
        Intent.EXTRA_SUBJECT,
        "Feedback App ${resources.getString(R.string.app_name)}"
    )
    //emailIntent.setSelector(selectorIntent);
    startActivity(Intent.createChooser(emailIntent, "Send via"))
}

fun Context.shareApp() {
    val myIntent = Intent(Intent.ACTION_SEND)
    myIntent.type = "text/plain"
    var shareSub = "${getString(R.string.app_name)} - "
    shareSub += "https://play.google.com/store/apps/details?id=$packageName"
    myIntent.putExtra(Intent.EXTRA_TEXT, shareSub)
    startActivity(Intent.createChooser(myIntent, "Share using"))
}

fun Context.shareImage(path: String) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "*/*"
    val file = File(Uri.parse(path).path)
    val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
//    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
    share.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(share, "Share Image"))
}

fun deleteImage(path: String): Boolean {
    val file = File(path)
    return file.delete()
}

fun Bitmap.saveToAppStorage(context: Context): String {
    val newPath = saveTo(defaultAlbumPath(), "IMG_")
    val imageUri = FileProvider.getUriForFile(
        context, "${context.packageName}.provider", File(newPath)
    )
    return imageUri.toString()
}

fun Bitmap.saveTo(folderPath: String, prefix: String): String {
    val folder = File(folderPath)
    if (!folder.exists()) folder.mkdirs()

    val newFile = File(folder, prefix + System.currentTimeMillis().convertTime("hhmmssS") + ".jpg")
    try {
        val out = FileOutputStream(newFile)
        compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    log("saved: ${newFile.path}")
    return newFile.path
}

fun Context.getBitmapFromAsset(filePath: String): Bitmap? {
    val inputStream: InputStream
    var bitmap: Bitmap? = null
    try {
        inputStream = assets.open(filePath.replace("file:///android_asset/", ""))
        bitmap = BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        log(e.toString(), true)
    }
    return bitmap
}

fun defaultAlbumPath(): String {
    return if (Build.VERSION.SDK_INT >= 30) {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/PhotoEnhance"
    } else {
        Environment.getExternalStorageDirectory().path + "/PhotoEnhance"
    }
}

fun File.isHighResolution(): Boolean {
    log("size: ${length() / 1024} Kb")
    return this.length() / 1024 / 1024 > 1 //size over 1Mb
}

const val REQUEST_DELETE_FILE = 245

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.deleteApi30(path: String) {
    val uri = getImageUriFromPath(path)
    log("uri to delete $uri")
    if (uri == null) return
    val pi = MediaStore.createDeleteRequest(contentResolver, listOf(uri))

    try {
        ActivityCompat.startIntentSenderForResult(
            this, pi.intentSender, REQUEST_DELETE_FILE, null, 0, 0,
            0, null
        )
    } catch (e: IntentSender.SendIntentException) {
        e.printStackTrace()
    }
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun pxToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.deleteMultipleApi30(paths: List<String>) {
    val uris = paths.map { getImageUriFromPath(it) }
    log("uri to delete $uris")
    val pi = MediaStore.createDeleteRequest(contentResolver, uris)

    try {
        ActivityCompat.startIntentSenderForResult(
            this, pi.intentSender, REQUEST_DELETE_FILE, null, 0, 0,
            0, null
        )
    } catch (e: IntentSender.SendIntentException) {
        e.printStackTrace()
    }
}

fun Context.getImageUriFromPath(path: String): Uri? {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA
    )

    contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        MediaStore.Video.VideoColumns.DATA + " LIKE ?",
        arrayOf(path),
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(idIndex)
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            cursor.close()
            return uri
        }
    }
    return null
}

fun Bitmap.getResizedBitmap(maxSize: Int): Bitmap? {
    var w = width
    var h = height
    val bitmapRatio = w.toFloat() / h.toFloat()
    if (bitmapRatio > 1) {
        w = maxSize
        h = (w / bitmapRatio).toInt()
    } else {
        h = maxSize
        w = (h * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(this, w, h, true)
}

fun Context.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
}

fun Double.toCurrency(pattern: String = "#,###"): String {
    return DecimalFormat(pattern).format(this)
}

fun runDelay(runnable: Runnable, time: Long = 1000) {
    Handler(Looper.getMainLooper()).postDelayed(runnable, time)
}

fun Long.convertSize(): String {
    val sizeInKb = this / 1024.0 //convert to KB
    return when {
        sizeInKb >= 1024 * 1024 -> {
            DecimalFormat("0.00").format(sizeInKb / 1024 / 1024).plus(" GB")
        }
        sizeInKb >= 1024 -> {
            DecimalFormat("0.00").format(sizeInKb / 1024).plus(" MB")
        }
        else -> {
            DecimalFormat("0").format(sizeInKb).plus(" KB")
        }
    }
}

fun runTryCatch(function: () -> Unit) {
    try {
        function()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun File.getSize(): Long {
    return try {
        if (isDirectory) FileUtils.sizeOfDirectory(this) else length()
    } catch (e: Exception) {
        0
    }
}
