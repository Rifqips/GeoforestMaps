package id.application.geoforestmaps.utils

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import id.application.geoforestmaps.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Constant {
    val IMAGE_FORMAT = "image/*"
    val IMAGE_PARSE = "image_parse"

    fun createFile(application: Application): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val outputDirectory = if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir
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

    fun Double.toDMS(): String {
        val degrees = this.toInt()
        val minutes = ((abs(this) - abs(degrees)) * 60).toInt()
        val seconds = ((abs(this) - abs(degrees)) * 60 - minutes) * 60

        return String.format("%d°%02d'%06.3f\"", degrees, minutes, seconds)
    }

    fun Pair<Double, Double>.toDMS(): Pair<String, String> {
        val latDMS = this.first.toDMS()
        val lonDMS = this.second.toDMS()
        return Pair(latDMS, lonDMS)
    }
}