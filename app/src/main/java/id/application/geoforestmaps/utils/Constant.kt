package id.application.geoforestmaps.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import android.util.Log
import id.application.geoforestmaps.R
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Constant {
    val IMAGE_FORMAT = "image/*"
    private const val IMAGE_QUALITY = 100


    fun createFile(application: Application): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val outputDirectory =
            if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir
        val imageFile = File(outputDirectory, "$timeStamp.jpg")
        compressAndSaveImage(imageFile)

        return imageFile
    }

    fun compressAndSaveImage(file: File) {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 80
        }
        val bitmap = BitmapFactory.decodeFile(file.path, options)
        bitmap?.let {
            val fos = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.close()
        }
    }

    @SuppressLint("NewApi")
    fun ZonedDateTime.formatDate(): String {
        return this.toLocalDate().toString()
    }

    @SuppressLint("NewApi")
    fun ZonedDateTime.formatTime(): String {
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        return this.toLocalTime().format(timeFormatter)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null
                && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ))
    }


    fun saveFile(responseBody: ResponseBody, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete() // Menghapus file yang sudah ada
        }
        file.outputStream().use { outputStream ->
            responseBody.byteStream().copyTo(outputStream)
        }
    }

    fun generateUniqueFileName(fileName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val extension = fileName.substringAfterLast(".", "")
        val baseName = fileName.substringBeforeLast(".")
        return "${baseName}_$timestamp.$extension"
    }

    fun generateFileName(baseName: String, extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return "$baseName$timestamp$extension"
    }


    /**
     * Convert image to base64 format by
     * @param photoPath
     */
    fun convertImageToBase64(file: File): String {
        return try {
            val byteArray = file.readBytes()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("IMAGE-BASE64", "convertImageToBase64: ", e.cause)
            ""
        }
    }


}