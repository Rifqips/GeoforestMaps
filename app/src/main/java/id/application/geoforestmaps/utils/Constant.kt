package id.application.geoforestmaps.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import id.application.geoforestmaps.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Constant {
    val IMAGE_FORMAT = "image/*"
    val IMAGE_PARSE = "image_parse"

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

}