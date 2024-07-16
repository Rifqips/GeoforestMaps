package id.application.core.data.network.model.login


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RequestLoginItem(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)