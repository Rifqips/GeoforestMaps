package id.application.geoforestmaps.presentation.feature.databasemaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.GpsStatus
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.geotags.ItemAllGeotaging
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentMapsBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import id.application.geoforestmaps.utils.NetworkCallback
import id.application.geoforestmaps.utils.NetworkChangeReceiver
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File

class MapsFragment : BaseFragment<FragmentMapsBinding, VmApplication>(FragmentMapsBinding::inflate),
    MapListener, GpsStatus.Listener, NetworkCallback {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseListAdapterItem by lazy {
        DatabaseListAdapterItem {
            addMarkersToMap(it.latitude, it.longitude)
        }
    }

    private val networkChangeReceiver: NetworkChangeReceiver by lazy {
        val refreshDataCallback = { refreshData() }
        getKoin().get<NetworkChangeReceiver> { parametersOf(refreshDataCallback) }
    }

    var blockName: String? = ""

    private val PERMISSIONS_REQUEST_CODE = 1
    lateinit var controller: IMapController

    @SuppressLint("SetTextI18n")
    override fun initView() {
        requireActivity().registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        with(binding) {
            topbar.ivTitle.text = "Map"
        }
        if (isNetworkAvailable(requireContext())) {
            setUpPaging()
            binding.layoutNoSignal.root.isGone = true
            lifecycleScope.launch {
                delay(2000)
                initVm()
            }
            onBackPressed()
            configMap()
            checkPermissions()
        } else {
            binding.pbLoading.isGone = true
            binding.layoutNoSignal.root.isGone = false
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.text_no_internet_connection),
                R.style.failedtoast
            ).show()
        }
    }

    override fun initListener() {
        with(binding) {
            topbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
                clearTrafficPaging()
            }
        }
    }

    private fun initVm() {
        viewModel.getBlockName()
        viewModel.isBlockName.observe(viewLifecycleOwner) { blockName ->
            this.blockName = blockName
            loadPagingGeotagingAdapter(adapterPagingGeotagging, blockName)
        }
    }

    private fun configMap() {
        // Initialize the Configuration instance
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        // Initialize the map controller
        controller = binding.map.controller


    }

    private fun loadPagingGeotagingAdapter(adapter: DatabaseListAdapterItem, blockName: String) {
        viewModel.loadPagingGeotagging(
            adapter,
            blockName
        )
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
                        val items = adapterPagingGeotagging.snapshot().items
                        addMarkersFromPagingData(items)
                    }
                }
            }
        }
    }
    private fun addMarkersFromPagingData(items: List<ItemAllGeotaging>) {
        // Clear existing markers if needed
        binding.map.overlays.clear()

        if (items.isNotEmpty()) {
            // Ambil elemen pertama dari daftar items
            val firstItem = items.first()
            val firstPoint = GeoPoint(firstItem.latitude, firstItem.longitude)

            // Set pusat peta dan zoom
            controller.setCenter(firstPoint)
            controller.setZoom(20.0)
            Log.d("check-lokasi", "Setting center to lat ${firstItem.latitude} lon ${firstItem.longitude}")

            // Tambahkan marker untuk setiap item
            items.forEach { item ->
                addMarkersToMap(item.latitude, item.longitude)
                Log.d("check-lokasi", "lat ${item.latitude} lon ${item.longitude}")
            }
        } else {
            Log.d("check-lokasi", "No items available to set center.")
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addMarkersToMap(lat: Double, lon: Double) {
        val marker = Marker(binding.map)
        marker.position = GeoPoint(lat, lon)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = resources.getDrawable(R.drawable.ic_marker_purple, null)
        binding.map.overlays.add(marker)
        binding.map.invalidate()
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
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDetach()
        requireActivity().unregisterReceiver(networkChangeReceiver)
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
        Log.e("TAG", "onScroll: la ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onScroll: lo ${event?.source?.getMapCenter()?.longitude}")
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
                    findNavController().popBackStack()
                    clearTrafficPaging()
                }
            }
        )
    }

    private fun clearTrafficPaging() {
        viewModel.geotaggingListAll.removeObservers(viewLifecycleOwner)
        binding.rvGeotaging.adapter = null
    }

    override fun onNetworkAvailable() {
        refreshData()
    }

    fun refreshData() {
        setUpPaging()
        initVm()
        binding.pbLoading.isGone = true
        binding.layoutNoSignal.root.isGone = true
    }

}

