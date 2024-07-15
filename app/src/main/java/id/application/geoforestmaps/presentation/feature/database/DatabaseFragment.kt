package id.application.geoforestmaps.presentation.feature.database

import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.model.Dashboard
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseBinding
import id.application.geoforestmaps.presentation.feature.database.AreaData.listDataArea
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseFragment :
    BaseFragment<FragmentDatabaseBinding, VmApplication>(FragmentDatabaseBinding::inflate) {

    private val adapterDatabase = DatabaseListAdapter()
    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        rvListDatabase()
    }

    override fun initListener() {}

    private fun rvListDatabase() {
        binding.rvBlokData.adapter = adapterDatabase
        binding.rvBlokData.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterDatabase.setData(listDataArea)
    }

}


object AreaData {
    private var images = intArrayOf(
        R.drawable.img_location,
        R.drawable.img_location
    )

    private var names = arrayOf(
        "Blok A",
        "Blok B"
    )

    val listDataArea : ArrayList<Dashboard>
        get() {
            val listData = arrayListOf<Dashboard>()
            for (position in names.indices){
                val dataDash = Dashboard()
                dataDash.image = images[position]
                dataDash.name = names[position]
                listData.add(dataDash)
            }
            return listData
        }
}