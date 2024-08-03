package id.application.geoforestmaps.presentation.feature.history

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.geotags.ItemAllGeotagingOffline
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHistoryBinding
import id.application.geoforestmaps.presentation.adapter.history.AdapterGeotagingLocal
import id.application.geoforestmaps.presentation.adapter.history.AdapterGeotagingOffline
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import id.application.geoforestmaps.utils.NetworkCallback
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, VmApplication>(FragmentHistoryBinding::inflate),
    NetworkCallback {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingLocalGeotagging: AdapterGeotagingLocal by lazy {
        AdapterGeotagingLocal {}
    }
    private val adapterGeotaggingOffline: AdapterGeotagingOffline by lazy {
        AdapterGeotagingOffline {}
    }

    override fun initView() {
        rvOffline()
        setUpPaging()
        if (!isNetworkAvailable(requireContext())) {
            binding.pbLoading.isGone = true
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.text_no_internet_connection),
                R.style.failedtoast
            ).show()
        }
    }

    override fun initListener() {
    }

    private fun setUpPaging() {
        if (view != null) {
            viewModel.fetchAllGeotagingLocal()
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.geotagingLocalResult.observe(viewLifecycleOwner) {
                    adapterPagingLocalGeotagging.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }

        adapterPagingLocalGeotagging.addLoadStateListener { loadState ->
            with(binding) {
                if (loadState.refresh is LoadState.Loading) {
                    pbLoading.visibility = View.VISIBLE
                } else {
                    pbLoading.visibility = View.GONE
                    if (view != null) {
                        rvHistoryAlreadySentData.apply {
                            layoutManager = LinearLayoutManager(context).apply {
                                isSmoothScrollbarEnabled = true
                            }
                            adapter = adapterPagingLocalGeotagging

                        }
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


