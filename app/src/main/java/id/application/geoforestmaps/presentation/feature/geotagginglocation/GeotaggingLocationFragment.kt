package id.application.geoforestmaps.presentation.feature.geotagginglocation

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.databinding.FragmentGeotaggingLocationBinding
import id.application.geoforestmaps.presentation.feature.database.AreaData.listDataArea
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeotaggingLocationFragment :
    BaseFragment<FragmentGeotaggingLocationBinding, VmApplication>
        (FragmentGeotaggingLocationBinding::inflate) {

    private val adapterGeotagging = GeotaggingListAdapter()
    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        with(binding){
            topBar.ivTitle.text = "Ambil Data"
        }
        rvListDatabase()
    }

    override fun initListener() {
        with(binding){
            topBar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun rvListDatabase() {
        binding.rvBlokData.adapter = adapterGeotagging
        binding.rvBlokData.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterGeotagging.setData(listDataArea)
    }

}