package id.application.geoforestmaps.presentation.feature.geotagginglocation

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentGeotaggingLocationBinding
import id.application.geoforestmaps.presentation.adapter.blocks.DatabaseAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeotaggingLocationFragment :
    BaseFragment<FragmentGeotaggingLocationBinding, VmApplication>
        (FragmentGeotaggingLocationBinding::inflate) {

    private val adapterPagingDatabase: DatabaseAdapterItem by lazy {
        DatabaseAdapterItem{navigateToCamera(it)}
    }

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        with(binding){
            topBar.ivTitle.text = "Ambil Data"
        }
        loadPagingBlocks(adapter = adapterPagingDatabase)
        setUpPaging()
    }

    override fun initListener() {
        with(binding){
            topBar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun loadPagingBlocks(
        adapter: DatabaseAdapterItem,
    ) {
        viewModel.loadPagingBlocks(
            adapter,
        )
    }

    private fun setUpPaging(){
        if (view != null){
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.blockList.observe(it) { pagingData ->
                    adapterPagingDatabase.submitData(lifecycle, pagingData)
                }
            }
        }
        adapterPagingDatabase.addLoadStateListener { loadState ->
            with(binding){
                if (loadState.refresh is LoadState.Loading) {
                    pbLoading.visibility = View.VISIBLE
                } else {
                    pbLoading.visibility = View.GONE
                    if (view != null){
                        rvBlokData.apply {
                            layoutManager = LinearLayoutManager(context).apply {
                                isSmoothScrollbarEnabled = true
                            }
                            adapter = adapterPagingDatabase
                        }
                    }
                }
            }
        }
    }

    private fun navigateToCamera(itemAllBlocks : ItemAllBlocks){
        val bundle = Bundle()
        bundle.putString("title", itemAllBlocks.name)
        bundle.putInt("ID_BLOCK", itemAllBlocks.id)
        val navController =
            activity?.supportFragmentManager
                ?.findFragmentById(R.id.container_navigation)?.findNavController()
        navController?.navigate(R.id.action_geotaggingLocationFragment_to_cameraFragment, bundle)
    }

}