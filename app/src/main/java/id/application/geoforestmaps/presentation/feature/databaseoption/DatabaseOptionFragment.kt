package id.application.geoforestmaps.presentation.feature.databaseoption

import android.os.Bundle
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
        { list -> navigateToFragment(R.id.action_databaseOptionFragment_to_databaseListFragment) },
        { gallery -> navigateToFragment(R.id.action_databaseOptionFragment_to_databaseGalleryFragment) },
        { map -> navigateToFragment(R.id.action_databaseOptionFragment_to_mapsFragment) }
    )

    override val viewModel: VmApplication by viewModel()
    private var blockName = ""

    override fun initView() {
        val title = arguments?.getString("title")
        if (title != null) {
            this.blockName = title
        }
        with(binding){
            topbar.ivTitle.text = title
            rvDatabaseOption.adapter = adapter
            rvDatabaseOption.layoutManager = LinearLayoutManager(requireContext())
            adapter.setData(listDataDatabaseOption)
        }
    }

    override fun initListener() {
        with(binding){
            topbar.ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    private fun navigateToFragment(actionId: Int) {
        val bundle = Bundle().apply {
            putString("blockName", blockName)
        }
        findNavController().navigate(actionId, bundle)
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