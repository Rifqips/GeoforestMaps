package id.application.geoforestmaps.presentation.feature.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.BuildConfig
import id.application.geoforestmaps.databinding.FragmentMapsBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

class MapsFragment : BaseFragment<FragmentMapsBinding, VmApplication>(FragmentMapsBinding::inflate) {

    override val viewModel: VmApplication by viewModel()
    private val PERMISSIONS_REQUEST_CODE = 1
    private lateinit var mapView: MapView

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.topbar.ivTitle.text = "Map"
        val userAgentValue = "${requireContext().packageName}/${BuildConfig.VERSION_NAME}"
        Configuration.getInstance().userAgentValue = userAgentValue
        Configuration.getInstance().load(requireContext(),
            requireContext().getSharedPreferences("osmdroid", 0))
        mapView = binding.map
        checkPermissions()
        val offlineTileProvider = setupOfflineTileProvider()
        if (offlineTileProvider != null) {
            mapView.setTileProvider(offlineTileProvider)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
        } else {
            mapView.setTileSource(TileSourceFactory.MAPNIK)
        }

        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val startPoint = GeoPoint(48.8583, 2.2944) // Eiffel Tower, Paris
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)

        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Eiffel Tower"
        mapView.overlays.add(marker)
    }

    override fun initListener() {
        binding.topbar.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupOfflineTileProvider(): OfflineTileProvider? {
        val basePath = File("/sdcard/osmdroid/")
        val archiveFile = File(basePath, "your-offline-map-file.zip")
        return if (archiveFile.exists()) {
            try {
                val receiver = SimpleRegisterReceiver(requireContext())
                val fileProvider = OfflineTileProvider(receiver, arrayOf(archiveFile))
                val tileSource = TileSourceFactory.MAPNIK
                mapView.setTileSource(tileSource)
                mapView.tileProvider = fileProvider
                fileProvider
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }


    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) !=
                    PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
