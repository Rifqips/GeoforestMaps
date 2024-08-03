package id.application.geoforestmaps.presentation.feature.history

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.History
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHistoryBinding
import id.application.geoforestmaps.presentation.adapter.history.AdapterGeotagingLocal
import id.application.geoforestmaps.presentation.adapter.history.AdapterGeotagingOffline
import id.application.geoforestmaps.presentation.adapter.history.HistoryListAdapter
import id.application.geoforestmaps.presentation.feature.history.HistoryData.listDataHistory
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, VmApplication>(FragmentHistoryBinding::inflate) {

    private val adapterHistory = HistoryListAdapter()
    override val viewModel: VmApplication by viewModel()

    private val adapterPagingLocalGeotagging: AdapterGeotagingLocal by lazy {
        AdapterGeotagingLocal {}
    }
    private val adapterGeotaggingOffline: AdapterGeotagingOffline by lazy {
        AdapterGeotagingOffline {}
    }

    override fun initView() {
        rvOffline()
//        rvListHistory()
        setUpPaging()
        if (isNetworkAvailable(requireContext())) {
            binding.layoutNoSignal.root.isGone = true
        } else {
            binding.layoutNoSignal.root.isGone = true
            binding.pbLoading.isGone = true
            StyleableToast.makeText(
                requireContext(),
                getString(R.string.text_no_internet_connection),
                R.style.failedtoast
            ).show()
        }

    }

    override fun initListener() {}

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

    private fun rvOffline(){
        // geotaging offline
        if (view != null) {
            viewModel.geotagingListOffline
            viewModel.geotagingListOffline.observe(viewLifecycleOwner){ result ->
                result.proceedWhen (
                    doOnLoading = {
                        binding.rvHistoryData.visibility = View.GONE
                        binding.pbLoadingOffline.visibility = View.VISIBLE
                        binding.cvDataSynchronization.visibility = View.VISIBLE
                    },
                    doOnSuccess = {
                        binding.rvHistoryData.visibility = View.VISIBLE
                        binding.pbLoadingOffline.visibility = View.GONE
                        binding.cvDataSynchronization.visibility = View.VISIBLE
                        binding.rvHistoryData.apply {
                            layoutManager = LinearLayoutManager(requireContext()).apply {
                                isSmoothScrollbarEnabled = true
                            }
                            adapter = adapterGeotaggingOffline
                        }
                        result.payload?.let {
                            adapterGeotaggingOffline.setData(it)
                        }
                    },
                    doOnEmpty = {
                        binding.rvHistoryData.visibility = View.GONE
                        binding.cvDataSynchronization.visibility = View.GONE
                        binding.pbLoadingOffline.visibility = View.GONE
                    }
                )
            }
        }

    }
//    private fun rvListHistory() {
//        binding.rvHistoryData.adapter = adapterHistory
//        binding.rvHistoryData.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        adapterHistory.setData(listDataHistory)
//    }

}


object HistoryData {

    private var images = intArrayOf(
        R.drawable.img_red_tree,
        R.drawable.img_red_tree,
        R.drawable.img_red_tree
    )

    private var titles = arrayOf(
        "Blok X",
        "Blok Y",
        "Blok Z"
    )

    private var descriptions = arrayOf(
        "Karet",
        "Kayu",
        "Daun"
    )

    private var times = arrayOf(
        "12:13 PM",
        "11:00 PM",
        "01:00 AM"
    )

    private var dates = arrayOf(
        "12 Mei 2024",
        "12 Juli 2024",
        "13 Juli 2024"
    )

    val listDataHistory: ArrayList<History>
        get() {
            val listHistory = arrayListOf<History>()
            for (position in titles.indices) {
                val dataHistory = History()
                dataHistory.image = images[position]
                dataHistory.title = titles[position]
                dataHistory.description = descriptions[position]
                dataHistory.time = times[position]
                dataHistory.date = dates[position]
                listHistory.add(dataHistory)
            }
            return listHistory
        }
}
