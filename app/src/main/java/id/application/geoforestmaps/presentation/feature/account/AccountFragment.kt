package id.application.geoforestmaps.presentation.feature.account

import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentAccountBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment :
    BaseFragment<FragmentAccountBinding, VmPreLogin>(FragmentAccountBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {}

    override fun initListener() {
        with(binding){
            btnLogout.setOnClickListener {
                activity?.supportFragmentManager?.
                findFragmentById(R.id.container_navigation)
                    ?.findNavController()?.navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

}