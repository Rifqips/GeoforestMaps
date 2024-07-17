package id.application.geoforestmaps.presentation.feature.dashboard

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import id.application.core.domain.model.Dashboard
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDashboardBinding
import id.application.geoforestmaps.presentation.feature.dashboard.DashboardData.listDataDash
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, VmApplication>(FragmentDashboardBinding::inflate) {

    private val adapterDashboard = DashboardCardListAdapter()

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        observeVM()
    }

    override fun initListener() {}

    @SuppressLint("SetTextI18n")
    private fun observeVM() {
        with(viewModel){
            userProfile()
            userProfileResult.observe(viewLifecycleOwner){ result ->
                result.proceedWhen(
                    doOnSuccess = {
                        it.payload?.data.let {
                            with(binding){
                                tvTitleDescription.text = "Halo, " + it?.name
                            }
                        }
                    }
                )
            }
        }
    }


    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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