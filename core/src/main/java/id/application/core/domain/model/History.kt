package id.application.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    var image : Int = 0,
    var title : String = "",
    var description : String = "",
    var time : String = "",
    var date : String = ""
): Parcelable
