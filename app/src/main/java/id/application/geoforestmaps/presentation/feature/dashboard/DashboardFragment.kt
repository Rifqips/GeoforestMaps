package id.application.geoforestmaps.presentation.feature.dashboard

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDashboardBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment :
    BaseFragment<FragmentDashboardBinding, VmApplication>(FragmentDashboardBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        observeVM()
    }

    override fun initListener() {}

    @SuppressLint("SetTextI18n")
    private fun observeVM() {
        with(viewModel){
            getUserName()
            viewModel.isUserName.observe(viewLifecycleOwner){
                with(binding){
                    tvTitleDescription.text = "Halo, " + it
                    it.let { name ->
                        if (name.isNotEmpty()) {
                            tvUserIcon.text = name[0].toString()
                        } else {
                            tvUserIcon.text = ""
                        }
                    }
                }
            }
        }
    }


    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

}