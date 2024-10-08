package id.application.core.data.network.model.profile


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.application.core.domain.model.profile.UserProfileResponse

@Keep
data class ResponseProfileItem(
    @SerializedName("data")
    val `data`: DataProfile
)
@Keep
data class DataProfile(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

fun ResponseProfileItem.toProfileResponse() = UserProfileResponse(
    data = data
)