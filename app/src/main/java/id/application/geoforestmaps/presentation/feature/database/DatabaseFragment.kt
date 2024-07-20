package id.application.geoforestmaps.presentation.feature.database

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseBinding
import id.application.geoforestmaps.presentation.adapter.blocks.DatabaseAdapterItem
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseFragment :
    BaseFragment<FragmentDatabaseBinding, VmApplication>(FragmentDatabaseBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingDatabase: DatabaseAdapterItem by lazy {
        DatabaseAdapterItem{
            navigateToDatabaseOption(it)
        }
    }

    override fun initView() {
        loadPagingBlocks(adapter = adapterPagingDatabase)
        setUpPaging()

    }

    override fun initListener() {}

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

    private fun navigateToDatabaseOption(itemAllBlocks : ItemAllBlocks){
        val bundle = Bundle()
        bundle.putString("title", itemAllBlocks.name)
        val navController =
            activity?.supportFragmentManager
                ?.findFragmentById(R.id.container_navigation)?.findNavController()
        navController?.navigate(R.id.action_homeFragment_to_databaseOptionFragment, bundle)
    }
}
