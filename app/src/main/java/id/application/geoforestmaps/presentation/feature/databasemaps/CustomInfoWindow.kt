package id.application.geoforestmaps.presentation.feature.databasemaps

import android.widget.TextView
import id.application.geoforestmaps.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(
    mapView: MapView,
    private val koordinatResult: String,
    private val blokText: String,
    private val tanamanText: String,
    private val userText: String,
    private val timestampText: String
) : InfoWindow(R.layout.custom_info_window, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as Marker
        val koordinat = mView.findViewById<TextView>(R.id.tv_koordinat)
        val blok = mView.findViewById<TextView>(R.id.tv_block_window)
        val tanaman = mView.findViewById<TextView>(R.id.tv_plant_type)
        val user = mView.findViewById<TextView>(R.id.tv_user)
        val timestamp = mView.findViewById<TextView>(R.id.tv_timestamp)

        koordinat.text = "Koordinat :\n$koordinatResult "
        blok.text = "Blok :\n$blokText "
        tanaman.text = "Tanaman :\n$tanamanText "
        user.text = "User :\n$userText "
        timestamp.text = "Timestamp :\n$timestampText "
    }

    override fun onClose() {
        // Aksi saat InfoWindow ditutup, bisa dibiarkan kosong
    }
}
