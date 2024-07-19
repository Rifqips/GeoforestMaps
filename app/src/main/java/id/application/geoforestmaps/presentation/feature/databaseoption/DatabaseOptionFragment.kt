package id.application.geoforestmaps.presentation.feature.databaseoption

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.databinding.FragmentDatabaseOptionBinding
import id.application.geoforestmaps.presentation.feature.dashboard.DashboardData.listDataDash
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseOptionFragment :
    BaseFragment<FragmentDatabaseOptionBinding, VmApplication>(FragmentDatabaseOptionBinding::inflate) {

    private val adapter = DatabaseOptionAdapter()

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        val title = arguments?.getString("title")
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
}