package id.application.geoforestmaps.presentation.feature.history

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHistoryBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.adapter.history.AdapterGeotagingOffline
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.formatDate
import id.application.geoforestmaps.utils.Constant.formatDateTime
import id.application.geoforestmaps.utils.Constant.formatTime
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import id.application.geoforestmaps.utils.Constant.showDialogDetail
import id.application.geoforestmaps.utils.NetworkCallback
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("ResourceType")
@RequiresApi(Build.VERSION_CODES.O)
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, VmApplication>(FragmentHistoryBinding::inflate),
    NetworkCallback {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseListAdapterItem by lazy {
        DatabaseListAdapterItem {
            val (formattedDate, formattedTime) = formatDateTime(it.createdAt)
            showDialogDetail(
                layoutInflater = layoutInflater,
                context = requireContext(),
                gallery = it.photo,
                itemDescription = it.block,
                itemTitle = it.plant,
                createdBy = "Dibuat oleh : ${it.user}",
                tvDateTime = formattedDate,
                tvTimeItem = formattedTime
            )
        }
    }

    private val adapterGeotaggingOffline: AdapterGeotagingOffline by lazy {
        AdapterGeotagingOffline {
            val (formattedDate, formattedTime) = formatDateTime(it.createdAt.toString())
            showDialogDetail(
                layoutInflater = layoutInflater,
                context = requireContext(),
                gallery = resources.getString(R.drawable.ic_img_loading),
                itemDescription = it.block,
                itemTitle = it.plant,
                createdBy = "Dibuat oleh : ${it.user}",
                tvDateTime = formattedDate,
                tvTimeItem = formattedTime
            )
        }
    }

    override fun initView() {
        rvOffline()
        if (isNetworkAvailable(requireContext())) {
            binding.layoutNoSignal.root.isGone = true
            setup()
        } else {
            binding.layoutNoSignal.root.isGone = false
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.text_no_internet_connection),
                R.style.failedtoast
            ).show()
        }
    }

    override fun initListener() {}

    private fun setup() {
        setUpPaging()
        loadPagingGeotaging(adapterPagingGeotagging)
    }

    private fun loadPagingGeotaging(adapter: DatabaseListAdapterItem) {
        viewModel.loadPagingGeotagging(adapter, createdBy = "user")
    }
    private fun setUpPaging() {
        viewModel.geotaggingList.observe(viewLifecycleOwner) { pagingData ->
            adapterPagingGeotagging.submitData(lifecycle, pagingData)
        }
        binding.rvHistoryAlreadySentData.apply {
            adapter = adapterPagingGeotagging
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        adapterPagingGeotagging.addLoadStateListener { loadState ->
            with(binding) {
                when (loadState.refresh) {
                    is LoadState.Loading -> {
//                        pbLoading.visibility = View.VISIBLE
                    }

                    is LoadState.NotLoading -> {
//                        pbLoading.visibility = View.GONE
                    }

                    is LoadState.Error -> {
//                        pbLoading.visibility = View.GONE
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun rvOffline() {
        if (view != null) {
            viewModel.geotagingListOffline.observe(viewLifecycleOwner) { result ->
                result.proceedWhen(
                    doOnSuccess = {
                        binding.rvHistoryData.visibility = View.VISIBLE
                        binding.tvTotalSentOffline.visibility = View.VISIBLE
                        binding.tvAlreadySent.visibility = View.VISIBLE
                        binding.cvDataSynchronization.visibility = View.VISIBLE
                        binding.rvHistoryData.apply {
                            layoutManager = LinearLayoutManager(requireContext()).apply {
                                isSmoothScrollbarEnabled = true
                            }
                            adapter = adapterGeotaggingOffline
                        }
                        result.payload?.let { geotaggingList ->
                            adapterGeotaggingOffline.setData(geotaggingList)
                            binding.cvSycnGeotagging.setOnClickListener {
                                syncGeotaggingWithDelay(geotaggingList)
                            }
                        }
                    },
                    doOnEmpty = {
                        binding.rvHistoryData.visibility = View.GONE
                        binding.cvDataSynchronization.visibility = View.GONE
                        binding.tvTotalSentOffline.visibility = View.GONE
                        binding.tvAlreadySent.visibility = View.GONE
                    }
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun syncGeotaggingWithDelay(geotaggingList: List<ItemAllGeotagingOffline>) {
        val totalData = geotaggingList.size
        lifecycleScope.launch {
            geotaggingList.forEachIndexed { index, geotagging ->
                delay(3000)
                binding.tvTotalSentOffline.text = "Mengirim data : ${index+1} dari $totalData"
                sycnGeotagging(
                    id = geotagging.id,
                    plantId = geotagging.plantId,
                    blockId = geotagging.blockId,
                    latitude = geotagging.latitude,
                    longtitude = geotagging.longitude,
                    altitude = geotagging.altitude,
                    base64 = geotagging.base64
                )
            }
        }
    }

    private fun sycnGeotagging(id : Int, plantId : String, blockId : String, latitude : String,
                               longtitude : String, altitude : String, base64 : String){
        viewModel.createGeotaging(
            plantId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            blockId.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            latitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            longtitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            altitude.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
            photoBase64 = base64.toRequestBody("multipart/form-data".toMediaTypeOrNull()),
        )
        viewModel.state.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    viewModel.deleteGeotaggingById(id)
                },
                onFailure = { exception ->
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

    }

    override fun onNetworkAvailable() {
        if (isAdded && view != null) {
            binding.cvSycnGeotagging.visibility = View.VISIBLE
        }else{
            binding.cvSycnGeotagging.visibility = View.GONE
        }
    }
}


