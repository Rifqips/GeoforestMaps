package id.application.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DatabaseOption(
    var image : Int = 0,
    var overlay : Int = 0,
    var name : String = ""
): Parcelable

