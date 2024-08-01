package id.application.geoforestmaps.presentation.feature.databaseoption

import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.DatabaseOption
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseOptionBinding
import id.application.geoforestmaps.presentation.adapter.databaseoption.DatabaseOptionAdapter
import id.application.geoforestmaps.presentation.feature.databaseoption.DatabaseOptionData.listDataDatabaseOption
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseOptionFragment :
    BaseFragment<FragmentDatabaseOptionBinding, VmApplication>(FragmentDatabaseOptionBinding::inflate) {

    private val adapter = DatabaseOptionAdapter(
        { _ -> navigateToFragment(R.id.action_databaseOptionFragment_to_databaseListFragment) },
        { _ -> navigateToFragment(R.id.action_databaseOptionFragment_to_databaseGalleryFragment) },
        { _ -> navigateToFragment(R.id.action_databaseOptionFragment_to_mapsFragment) }
    )

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        initVm()
        onBackPressed()
        with(binding){
            rvDatabaseOption.adapter = adapter
            rvDatabaseOption.layoutManager = LinearLayoutManager(requireContext())
            adapter.setData(listDataDatabaseOption)
        }
    }

    private fun initVm() {
        viewModel.getBlockName()
        viewModel.isBlockName.observe(viewLifecycleOwner) { blockName ->
            binding.topbar.ivTitle.text = blockName
//            viewModel.deleteBlockName() kalo disini bisa ke triger hapus

        }
    }

    override fun initListener() {
        with(binding){
            topbar.ivBack.setOnClickListener {
                viewModel.deleteBlockName {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun navigateToFragment(actionId: Int) {
        findNavController().navigate(actionId)
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.deleteBlockName {
                        findNavController().navigateUp()
                    }
                }
            }
        )
    }
}

object DatabaseOptionData {
    private var images = intArrayOf(
        R.drawable.bg_list_geotag,
        R.drawable.bg_image_geotag,
        R.drawable.bg_map_geotag
    )

    private var overlays = intArrayOf(
        R.drawable.bg_gradient_list_geotag,
        R.drawable.bg_gradient_image_geotag,
        R.drawable.bg_gradient_map_geotag
    )

    private var names = arrayOf(
        "List Geotag",
        "Image Geotag",
        "Map Geotag"
    )

    val listDataDatabaseOption : ArrayList<DatabaseOption>
        get() {
            val listDataDatabaseOpt = arrayListOf<DatabaseOption>()
            for (position in names.indices){
                val dataDbOpt = DatabaseOption()
                dataDbOpt.image = images[position]
                dataDbOpt.overlay = overlays[position]
                dataDbOpt.name = names[position]

                listDataDatabaseOpt.add(dataDbOpt)
            }
            return listDataDatabaseOpt
        }

}