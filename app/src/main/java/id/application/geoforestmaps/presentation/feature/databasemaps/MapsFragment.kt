package id.application.geoforestmaps.presentation.feature.databasemaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.GpsStatus
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.DialogSaveDatabaseBinding
import id.application.geoforestmaps.databinding.FragmentMapsBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.generateFileName
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

class MapsFragment : BaseFragment<FragmentMapsBinding, VmApplication>(FragmentMapsBinding::inflate),
    MapListener, GpsStatus.Listener  {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseListAdapterItem by lazy {
        DatabaseListAdapterItem {
            addMarkersToMap(it.latitude, it.longitude)
        }
    }

    var block: String? = ""
    var blockName: String? = ""

    private val PERMISSIONS_REQUEST_CODE = 1
    private var activeDialog: AlertDialog? = null

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay

    @SuppressLint("SetTextI18n")
    override fun initView() {
        configMap()
        onBackPressed()
        with(binding){
            topbar.ivTitle.text = "Map"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        block = arguments?.getString("blockId")
        blockName = arguments?.getString("blockName")
        checkPermissions()

        // Load data dan setup adapter
        loadPagingGeotagingAdapter(adapterPagingGeotagging)
        setUpPaging()
    }

    override fun initListener() {
        with(binding) {
            topbar.ivBack.setOnClickListener {
                findNavController().navigate(R.id.action_mapsFragment_to_homeFragment)
            }
            topbar.ivDownlaod.setOnClickListener {
                exportFile()
            }
        }
    }

    private fun configMap() {
        // Initialize the Configuration instance
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        mMap = binding.map
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)

        // Initialize the map controller
        controller = mMap.controller

        // Set the map to display Indonesia
        val indonesiaCenter = GeoPoint(-5.0, 120.0)
        controller.setCenter(indonesiaCenter)
        controller.setZoom(5.0)

        // Setup MyLocationOverlay
        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireActivity()), mMap)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            lifecycleScope.launch {
                controller.setCenter(mMyLocationOverlay.myLocation)
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        mMap.overlays.add(mMyLocationOverlay)
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
        val fileName = generateFileName("geotaging-$blockName", ".xlsx")
        val file = File(downloadDir, fileName)
        viewModel.eksports(type = "list", block = block, null, file.name, requireContext())
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
            when(text){
                "Download berhasil!"->{
                    ivSuccess.load(R.drawable.ic_success)
                }
                "Download gagal!"->{
                    ivSuccess.load(R.drawable.ic_download_failed)
                }
            }
            when (state) {
                true -> {
                    pBar.visibility = View.VISIBLE
                    ivSuccess.visibility = View.GONE
                    ivClose.visibility = View.GONE
                }
                false -> {
                    pBar.visibility = View.GONE
                    ivSuccess.visibility = View.VISIBLE
                    ivClose.visibility = View.VISIBLE
                }
            }
            ivClose.setOnClickListener {
                dialog.dismiss()
                activeDialog = null
            }
        }
        binding.root.setOnTouchListener { _, _ ->
            true
        }
    }

    private fun loadPagingGeotagingAdapter(adapter: DatabaseListAdapterItem) {
        if (blockName != null) {
            viewModel.loadPagingGeotagging(
                adapter,
                blockName,
            )
        }
    }

    private fun setUpPaging() {
        if (view != null) {
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.geotaggingListAll.observe(it) { pagingData ->
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
                // Update markers after the data is loaded
                lifecycleScope.launch {
                    adapterPagingGeotagging.loadStateFlow.collect { loadState ->
                        if (loadState.refresh is LoadState.NotLoading) {
                            val items = adapterPagingGeotagging.snapshot().items
                            addMarkersFromPagingData(items)
                        }
                    }
                }
            }
        }
    }

    private fun addMarkersFromPagingData(items: List<ItemAllGeotaging>) {
        mMap.overlays.clear() // Clear existing markers if needed
        items.forEach { item ->
            addMarkersToMap(item.latitude, item.longitude)
        }
    }

    private fun addMarkersToMap(lat: Double, lon: Double) {
        val marker = Marker(mMap)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//        marker.icon = resources.getDrawable(R.drawable.ic_marker, null)
        marker.icon
        mMap.overlays.add(marker)
        mMap.invalidate()
    }

    private fun setupOfflineTileProvider(): OfflineTileProvider? {
        val basePath = File("/sdcard/osmdroid/")
        val archiveFile = File(basePath, "your-offline-map-file.zip")
        return if (archiveFile.exists()) {
            try {
                val receiver = SimpleRegisterReceiver(requireContext())
                val fileProvider = OfflineTileProvider(receiver, arrayOf(archiveFile))
                val tileSource = TileSourceFactory.MAPNIK
                mMap.setTileSource(tileSource)
                fileProvider
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    private fun setupOnlineTileProvider() {
        val tileSource = TileSourceFactory.MAPNIK
        mMap.setTileSource(tileSource)
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
        mMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMap.onDetach() // Membersihkan sumber daya MapView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Handle permission granted
            } else {
                // Handle permission denied
            }
        }
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onGpsStatusChanged(event: Int) {
        TODO("Not yet implemented")
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_mapsFragment_to_homeFragment)
                }
            }
        )
    }
}
