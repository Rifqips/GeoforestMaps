package id.application.core.domain.model.profile

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.application.core.data.network.model.profile.DataProfile
import id.application.core.data.network.model.profile.ResponseProfileItem

@Keep
data class UserProfileResponse(
    @SerializedName("data")
    val `data`: DataProfile
)

fun ResponseProfileItem.toProfileResponse() = UserProfileResponse(
    data = data
)