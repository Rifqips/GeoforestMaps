package id.application.geoforestmaps.presentation.feature.databaseoption

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.Dashboard
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseOptionBinding
import id.application.geoforestmaps.presentation.feature.dashboard.DashboardData.listDataDash
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseOptionFragment :
    BaseFragment<FragmentDatabaseOptionBinding, VmApplication>(FragmentDatabaseOptionBinding::inflate) {

    private val adapter = DatabaseOptionAdapter(){
        navigateToDatabaseOption(it)
    }

    override val viewModel: VmApplication by viewModel()
    private var blockId = ""

    override fun initView() {
        val title = arguments?.getString("title")
        var block: String? = arguments?.getString("blockId")
        if (block != null) {
            this.blockId = block
        }
        with(binding){
            topbar.ivTitle.text = title
        }
        rvListData()
    }

    override fun initListener() {
        with(binding){
            binding.topbar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun rvListData() {
        binding.rvDatabaseOption.adapter = adapter
        binding.rvDatabaseOption.layoutManager = LinearLayoutManager(requireContext())
        adapter.setData(listDataDash)
    }

    private fun navigateToDatabaseOption(dashboard : Dashboard){
        val bundle = Bundle()
        bundle.putString("blockId", blockId)
        findNavController().navigate(R.id.action_databaseOptionFragment_to_mapsFragment, bundle)
    }
}