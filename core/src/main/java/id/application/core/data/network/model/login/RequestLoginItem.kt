package id.application.core.data.network.model.login


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RequestLoginItem(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)