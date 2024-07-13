package id.application.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class HistoryAlreadySent(
    var image : Int = 0,
    var title : String = "",
    var description : String = "",
    var time : String = "",
    var date : String = ""
): Parcelable
