package id.application.geoforestmaps.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import id.application.geoforestmaps.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
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
            val base64Image = resizeAndCompressImage(file)
            "data:image/jpeg;base64,$base64Image"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun resizeAndCompressImage(file: File, maxFileSizeKB: Int = 1024): String {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, this)
        }

        val photoW = options.outWidth
        val photoH = options.outHeight

        val targetW = 800
        val targetH = 800

        val inSampleSize = calculateInSampleSize(photoW, photoH, targetW, targetH)

        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100
        do {
            byteArrayOutputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            quality -= 10
        } while (byteArrayOutputStream.toByteArray().size > maxFileSizeKB * 1024 && quality > 0)

        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun calculateInSampleSize(photoW: Int, photoH: Int, reqW: Int, reqH: Int): Int {
        var inSampleSize = 1
        if (photoH > reqH || photoW > reqW) {
            val halfHeight = photoH / 2
            val halfWidth = photoW / 2

            while (halfHeight / inSampleSize >= reqH && halfWidth / inSampleSize >= reqW) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

     fun formatAltitude(altitude: Double): String {
        val decimalFormat = DecimalFormat("#.#") // Format dengan 1 tempat desimal
        return decimalFormat.format(altitude)
    }

}