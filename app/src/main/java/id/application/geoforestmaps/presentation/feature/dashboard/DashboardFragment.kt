package id.application.geoforestmaps.presentation.feature.dashboard

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import id.application.core.domain.model.Dashboard
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.BuildConfig
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
        checkLoginResult()
        rvListData()
    }

    override fun initListener() {}

    private fun checkLoginResult() {
        viewModel.checkLogin()

        viewModel.isUserLogin.observe(viewLifecycleOwner) { isLogin ->
            if (!isLogin) {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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