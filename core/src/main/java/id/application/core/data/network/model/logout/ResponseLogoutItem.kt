package id.application.core.data.network.model.logout


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseLogoutItem(
    @SerializedName("message")
    val message: String
)