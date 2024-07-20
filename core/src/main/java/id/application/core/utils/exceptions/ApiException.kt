package id.application.core.utils.exceptions

import com.google.gson.Gson
import id.application.core.data.network.model.login.ResponseLoginItem
import retrofit2.Response


class ApiException(
    override val message: String?,
    val httpCode: Int,
    val errorResponse: Response<*>?
) : Exception() {

    fun getParsedErrorLogin(): ResponseLoginItem? {
        val body = errorResponse?.errorBody()?.string().orEmpty()
        return try {
            val bodyObj = Gson().fromJson(body, ResponseLoginItem::class.java)
            bodyObj
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}