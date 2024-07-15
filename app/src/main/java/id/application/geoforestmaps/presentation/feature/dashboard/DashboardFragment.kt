package id.application.geoforestmaps.presentation.feature.dashboard

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import id.application.core.model.Dashboard
import id.application.geoforestmaps.BuildConfig
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDashboardBinding
import id.application.geoforestmaps.presentation.feature.dashboard.DashboardData.listDataDash

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val adapterDashboard = DashboardCardListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvListData()
    }
    private fun rvListData() {
        binding.rvDashboardData.adapter = adapterDashboard
        binding.rvDashboardData.layoutManager = GridLayoutManager(requireContext(), 2)
        adapterDashboard.setData(listDataDash)
    }


}

object DashboardData {
    private var images = intArrayOf(
        R.drawable.ic_list_data,
        R.drawable.ic_image,
        R.drawable.ic_map
    )

    private var names = arrayOf(
        "List Data",
        "Image",
        "Map"
    )

    val listDataDash : ArrayList<Dashboard>
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