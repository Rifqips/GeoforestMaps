package id.application.geoforestmaps.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.load
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogConfirmCustomBinding
import id.application.geoforestmaps.databinding.DialogListDetailBinding
import okhttp3.OkHttpClient
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.zip.Inflater

object Constant {
    val IMAGE_FORMAT = "image/*"

    fun createFile(application: Application): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val outputDirectory =
            if (mediaDir != null && mediaDir.exists()) mediaDir else application.filesDir
        val imageFile = File(outputDirectory, "$timeStamp.jpg")

        return imageFile
    }

    fun compressAndSaveImage(file: File) {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val maxFileSize = 100 * 1024 // 100 KB
        var quality = 100
        var compressedFileSize: Int

        bitmap?.let {
            var tempFile = File(file.parent, "temp_${file.name}")
            var outputStream: FileOutputStream?

            do {
                outputStream = FileOutputStream(tempFile)
                it.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.close()

                compressedFileSize = tempFile.length().toInt()
                quality -= 5

                if (quality < 0) quality = 0

            } while (compressedFileSize > maxFileSize && quality > 0)

            if (compressedFileSize > maxFileSize) {
                val ratio = maxFileSize.toFloat() / compressedFileSize
                val finalWidth = (it.width * ratio).toInt()
                val finalHeight = (it.height * ratio).toInt()
                val finalBitmap = Bitmap.createScaledBitmap(it, finalWidth, finalHeight, true)

                FileOutputStream(tempFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
            }

            if (tempFile.exists()) {
                file.delete() // Delete the original file
                tempFile.renameTo(file) // Rename the compressed file to the original file name
            }
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

    fun resizeAndCompressImage(file: File, maxFileSizeKB: Int = 1024): String {
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


    @SuppressLint("ClickableViewAccessibility")
    fun showDialogDetail(
        layoutInflater: LayoutInflater,
        context: Context,
        itemTitle: String,
        itemDescription: String,
        gallery: String,
        createdBy: String,
        tvDateTime: String,
        tvTimeItem: String
    ) {
        val binding: DialogListDetailBinding = DialogListDetailBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context, 0).create()

        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.show()

        with(binding) {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    response
                }
                .build()
            val imageLoader = ImageLoader.Builder(context)
                .okHttpClient(client)
                .build()
            ivGallery.load(gallery, imageLoader) {
                placeholder(R.drawable.ic_img_loading) // Gambar sementara saat loading
                error(R.drawable.ic_img_failed) // Gambar saat gagal memuat
                crossfade(true)
            }
            // Set text fields
            tvTitleItemHistory.text = itemTitle
            tvDescItemHistory.text = itemDescription
            tvCreatedBy.text = createdBy
            tvDateItemHistory.text = tvDateTime
            tvTimeItemHistory.text = tvTimeItem
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateTime(dateString: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = ZonedDateTime.parse(dateString, formatter)
        return Pair(dateTime.formatDate(), dateTime.formatTime())
    }
}