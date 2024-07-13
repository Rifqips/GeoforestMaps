package id.application.geoforestmaps.presentation.feature.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.model.Dashboard
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentAreaBinding
import id.application.geoforestmaps.presentation.feature.area.AreaData.listDataArea

class AreaFragment : Fragment() {

    private lateinit var binding: FragmentAreaBinding
    private val adapterArea = AreaListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvListArea()
    }

    private fun rvListArea() {
        binding.rvBlokData.adapter = adapterArea
        binding.rvBlokData.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterArea.setData(listDataArea)
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