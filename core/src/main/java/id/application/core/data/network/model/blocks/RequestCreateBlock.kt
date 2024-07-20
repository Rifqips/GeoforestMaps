package id.application.core.data.network.model.blocks

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RequestCreateBlock(
    @SerializedName("name")
    val name: String
)