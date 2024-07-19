package id.application.geoforestmaps.presentation.feature.database

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import id.application.core.domain.model.Dashboard
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseFragment :
    BaseFragment<FragmentDatabaseBinding, VmApplication>(FragmentDatabaseBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    private val adapterPagingDatabase: DatabaseAdapterItem by lazy {
        DatabaseAdapterItem{}
    }

    override fun initView() {
        loadPagingBlocks(adapter = adapterPagingDatabase)
        setUpPaging()

    }

    override fun initListener() {}

    private fun loadPagingBlocks(
        adapter: DatabaseAdapterItem,
        brandItem: String? = null,
        sortItem: String? = null,
    ) {
        viewModel.loadPagingBlocks(
            adapter,
            brandItem?.lowercase(),
            sortItem?.lowercase(),
        )
    }

    private fun setUpPaging(){
        if (view != null){
            parentFragment?.viewLifecycleOwner?.let {
                viewModel.blockList.observe(it) { pagingData ->
                    adapterPagingDatabase.submitData(lifecycle, pagingData)
                }
            }
        }
        adapterPagingDatabase.addLoadStateListener { loadState ->
            with(binding){
                if (loadState.refresh is LoadState.Loading) {
                    pbLoading.visibility = View.VISIBLE
                } else {
                    pbLoading.visibility = View.GONE
                    if (view != null){
                        rvBlokData.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = adapterPagingDatabase
                        }
                    }
                }
            }
        }
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