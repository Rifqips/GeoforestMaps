package id.application.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dashboard(
    var image : Int = 0,
    var name : String = ""
): Parcelable
