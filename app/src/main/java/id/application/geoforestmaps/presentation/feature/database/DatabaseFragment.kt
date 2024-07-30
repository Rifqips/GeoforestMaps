package id.application.geoforestmaps.presentation.feature.database

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.blocks.ItemAllBlocks
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseBinding
import id.application.geoforestmaps.presentation.adapter.blocks.AdapterBlockLocal
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import id.application.geoforestmaps.utils.Constant.isNetworkAvailable
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseFragment :
    BaseFragment<FragmentDatabaseBinding, VmApplication>(FragmentDatabaseBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingLocalBlock: AdapterBlockLocal by lazy {
        AdapterBlockLocal {
            navigateToDatabaseOption(it)
        }
    }

    override fun initView() {
        setUpPaging()
        if (isNetworkAvailable(requireContext())) {
            binding.layoutNoSignal.root.isGone = true
        } else {
            binding.layoutNoSignal.root.isGone = false
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
            viewModel.fetchAllBlockLocal()
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.blockLocalResult.observe(viewLifecycleOwner) {
                    adapterPagingLocalBlock.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }
        adapterPagingLocalBlock.addLoadStateListener { loadState ->
            with(binding) {
                if (loadState.refresh is LoadState.Loading) {
                    pbLoading.visibility = View.VISIBLE
                } else {
                    pbLoading.visibility = View.GONE
                    if (view != null) {
                        rvBlokData.apply {
                            layoutManager = LinearLayoutManager(context).apply {
                                isSmoothScrollbarEnabled = true
                            }
                            adapter = adapterPagingLocalBlock

                        }
                    }
                }
            }
        }
    }

    private fun navigateToDatabaseOption(itemAllBlocks : ItemAllBlocks){
        val bundle = Bundle()
        bundle.putString("title", itemAllBlocks.name)
        bundle.putString("blockId", itemAllBlocks.id.toString())
        val navController =
            activity?.supportFragmentManager
                ?.findFragmentById(R.id.container_navigation)?.findNavController()
        navController?.navigate(R.id.action_homeFragment_to_databaseOptionFragment, bundle)
    }
}
