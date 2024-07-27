package id.application.geoforestmaps.presentation.feature.databaselist

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseListBinding
import id.application.geoforestmaps.presentation.adapter.databaselist.DatabaseListAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseListFragment :
    BaseFragment<FragmentDatabaseListBinding, VmApplication>(FragmentDatabaseListBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingGeotagging: DatabaseListAdapterItem by lazy {
        DatabaseListAdapterItem {}
    }

    var block: String? = ""
    var blockName: String? = ""

    override fun initView() {
        with(binding){
            topbar.ivTitle.text = "List"
            topbar.ivDownlaod.load(R.drawable.ic_download)
        }
        block = arguments?.getString("blockId")
        blockName = arguments?.getString("blockName")
        loadPagingGeotagingAdapter(adapterPagingGeotagging)
        setUpPaging()
    }

    override fun initListener() {}

    private fun loadPagingGeotagingAdapter(adapter: DatabaseListAdapterItem) {
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
                        topbar.ivDownlaod.visibility = View.GONE
                    } else {
                        pbLoading.visibility = View.GONE
                        topbar.ivDownlaod.visibility = View.VISIBLE
                        val isEmpty = (loadState.refresh is LoadState.NotLoading &&
                                adapterPagingGeotagging.itemCount == 0)
                        if (isEmpty) {
                            tvValidatingData.visibility = View.VISIBLE
                            tvValidatingData.text = "Belum ada data"
                        }

                        if (view != null) {
                            rvDatabaseList.apply {
                                layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
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

}