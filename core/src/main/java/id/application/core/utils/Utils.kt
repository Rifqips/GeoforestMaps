package id.application.core.utils

import okhttp3.ResponseBody
import java.io.File

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
}
