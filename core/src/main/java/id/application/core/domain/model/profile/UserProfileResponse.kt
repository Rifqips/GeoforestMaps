package id.application.core.domain.model.profile

import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.application.core.data.network.model.profile.DataProfile

@Keep
data class UserProfileResponse(
    @SerializedName("data")
    val `data`: DataProfile
)