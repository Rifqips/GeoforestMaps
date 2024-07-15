package id.application.geoforestmaps.presentation.feature.history

import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.model.History
import id.application.core.model.HistoryAlreadySent
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHistoryBinding
import id.application.geoforestmaps.presentation.feature.history.HistoryData.listDataHistory
import id.application.geoforestmaps.presentation.feature.history.HistoryData.listDataHistoryAlreadySent
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment :
    BaseFragment<FragmentHistoryBinding, VmApplication>(FragmentHistoryBinding::inflate)  {


    private val adapterHistory = HistoryListAdapter()
    private val adapterHistoryAlreadySentAdapter = HistoryAlreadySentAdapter()
    override val viewModel: VmApplication by viewModel()


    override fun initView() {
        rvListHistory()
        rvListHistoryAlreadySent()
    }

    override fun initListener() {}

    private fun rvListHistory() {
        binding.rvHistoryData.adapter = adapterHistory
        binding.rvHistoryData.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterHistory.setData(listDataHistory)
    }

    private fun rvListHistoryAlreadySent() {
        binding.rvHistoryAlreadySentData.adapter = adapterHistoryAlreadySentAdapter
        binding.rvHistoryAlreadySentData.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapterHistoryAlreadySentAdapter.setData(listDataHistoryAlreadySent)
    }

}


object HistoryData {

    private var images = intArrayOf(
        R.drawable.img_red_tree,
        R.drawable.img_red_tree,
        R.drawable.img_red_tree
    )

    private var titles = arrayOf(
        "Blok X",
        "Blok Y",
        "Blok Z"
    )

    private var descriptions = arrayOf(
        "Karet",
        "Kayu",
        "Daun"
    )

    private var times = arrayOf(
        "12:13 PM",
        "11:00 PM",
        "01:00 AM"
    )

    private var dates = arrayOf(
        "12 Mei 2024",
        "12 Juli 2024",
        "13 Juli 2024"
    )

    val listDataHistory : ArrayList<History>
        get() {
            val listHistory = arrayListOf<History>()
            for (position in titles.indices){
                val dataHistory = History()
                dataHistory.image = images[position]
                dataHistory.title = titles[position]
                dataHistory.description = descriptions[position]
                dataHistory.time = times[position]
                dataHistory.date = dates[position]
                listHistory.add(dataHistory)
            }
            return listHistory
        }


    private var images_already_sent = intArrayOf(
        R.drawable.img_green_tree,
        R.drawable.img_green_tree
    )

    private var titles_already_sent = arrayOf(
        "Blok X",
        "Blok Y"
    )

    private var descriptions_already_sent = arrayOf(
        "Karet",
        "Kayu"
    )

    private var times_already_sent = arrayOf(
        "02:13 AM",
        "10:00 PM"
    )

    private var dates_already_sent = arrayOf(
        "9 Mei 2024",
        "10 Juli 2024"
    )

    val listDataHistoryAlreadySent : ArrayList<HistoryAlreadySent>
        get() {
            val listHistoryAlreadySent = arrayListOf<HistoryAlreadySent>()
            for (position in titles_already_sent.indices){
                val dataHistoryAlreadySent = HistoryAlreadySent()
                dataHistoryAlreadySent.image = images_already_sent[position]
                dataHistoryAlreadySent.title = titles_already_sent[position]
                dataHistoryAlreadySent.description = descriptions_already_sent[position]
                dataHistoryAlreadySent.time = times_already_sent[position]
                dataHistoryAlreadySent.date = dates_already_sent[position]
                listHistoryAlreadySent.add(dataHistoryAlreadySent)
            }
            return listHistoryAlreadySent
        }
}