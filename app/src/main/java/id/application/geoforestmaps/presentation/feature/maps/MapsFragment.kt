package id.application.geoforestmaps.presentation.feature.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.core.utils.ResultWrapper
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentMapsBinding
import id.application.geoforestmaps.presentation.adapter.geotags.GeotaggingAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsFragment :
    BaseFragment<FragmentMapsBinding, VmApplication>(FragmentMapsBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: GeotaggingAdapterItem by lazy {
        GeotaggingAdapterItem {
            addMarkersToMap(it.latitude, it.longitude)
        }
    }
    var block: String? = ""

    private val PERMISSIONS_REQUEST_CODE = 1
    private lateinit var mapView: MapView
    private var activeDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mapView = binding.map
        binding.topbar.ivTitle.text = "Map"
        binding.topbar.ivDownlaod.load(R.drawable.ic_download)
        block = arguments?.getString("blockId")
        checkPermissions()

        // Setup tile provider offline (jika ada)
        val offlineTileProvider = setupOfflineTileProvider()
        if (offlineTileProvider != null) {
            mapView.setTileProvider(offlineTileProvider)
        }

        // Konfigurasi MapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Set titik awal peta
        val startPoint = GeoPoint(-6.9175, 107.6191)
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)

        // Load data dan setup adapter
        loadPagingGeotagingAdapter(adapterPagingGeotagging)
        setUpPaging()
    }

    override fun initListener() {
        with(binding) {
            topbar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            topbar.ivDownlaod.setOnClickListener {
                exportFile()
            }
        }


    }

    private fun exportFile() {
        viewModel.downloadStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                "Mendownload File..." -> {
                    showDialogConfirmSaveData(true, status)
                }

                "Download berhasil!" -> {
                    showDialogConfirmSaveData(false, status)
                }

                "Download gagal!" -> {
                    showDialogConfirmSaveData(false, status)
                }
            }
        })
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val fileName = generateFileName("geotaging$block", ".xlsx")
        val file = File(downloadDir, fileName)
        viewModel.eksports(type = "list", blockId = block?.toInt(), file.name, requireContext())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showDialogConfirmSaveData(state: Boolean, text: String) {
        activeDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        val binding: DialogSaveDatabaseBinding = DialogSaveDatabaseBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), 0).create()
        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }.show()
        activeDialog = dialog
        binding.dialogTitle.text = text
        with(binding) {
            when (state) {
                true -> {
                    pBar.visibility = View.VISIBLE
                    ivSuccess.visibility = View.GONE
                    tvSucese.visibility = View.GONE
                }

                false -> {
                    pBar.visibility = View.GONE
                    ivSuccess.visibility = View.VISIBLE
                    tvSucese.visibility = View.VISIBLE
                }
            }
            tvSucese.setOnClickListener {
                dialog.dismiss()
                activeDialog = null
            }
        }
        binding.root.setOnTouchListener { _, _ ->
            true
        }
    }

    private fun generateFileName(baseName: String, extension: String): String {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return "$baseName$timestamp$extension"
    }

    private fun loadPagingGeotagingAdapter(adapter: GeotaggingAdapterItem) {
        if (block != null) {
            viewModel.loadPagingGeotagging(
                adapter,
                block?.toInt(),
            )
        }
    }

    private fun setUpPaging() {
        if (view != null) {
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.geotaggingList.observe(it) { pagingData ->
                    adapterPagingGeotagging.submitData(lifecycle, pagingData)
                }
            }
            adapterPagingGeotagging.addLoadStateListener { loadState ->
                with(binding) {
                    if (loadState.refresh is LoadState.Loading) {
                        pbLoading.visibility = View.VISIBLE
                        binding.topbar.ivDownlaod.visibility = View.GONE
                    } else {
                        pbLoading.visibility = View.GONE
                        binding.topbar.ivDownlaod.visibility = View.VISIBLE
                        if (view != null) {
                            rvGeotaging.apply {
                                layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                ).apply {
                                    isSmoothScrollbarEnabled = true
                                }
                                adapter = adapterPagingGeotagging
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addMarkersToMap(lat: Double, lon: Double) {
        val marker = Marker(mapView)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        mapView.invalidate()
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
            ActivityCompat.requestPermissions(
                requireActivity(), permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach() // Membersihkan sumber daya MapView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportFile()
            } else {
            }
        }
    }

    companion object {
        private val REQUEST_WRITE_STORAGE = 112
    }

}


