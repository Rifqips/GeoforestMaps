package id.application.core.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import okhttp3.ResponseBody
import java.io.IOException

object Utils {

    fun saveFile(responseBody: ResponseBody, filePath: String) {
        val file = File(filePath)
        file.outputStream().use { outputStream ->
            responseBody.byteStream().use { inputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    fun unzip(filePath: String, destinationDir: String) {
        val file = File(filePath)
        val destDir = File(destinationDir)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        FileInputStream(file).use { fis ->
            ZipInputStream(fis).use { zis ->
                var zipEntry: ZipEntry? = zis.nextEntry
                while (zipEntry != null) {
                    val newFile = File(destDir, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile.mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            val buffer = ByteArray(1024)
                            var len: Int
                            while (zis.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                    }
                    zis.closeEntry()
                    zipEntry = zis.nextEntry
                }
            }
        }
    }

}