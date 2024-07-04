package id.application.geoforestmaps.presentation.feature.login

import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentLoginBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment :
    BaseFragment<FragmentLoginBinding, VmPreLogin>(FragmentLoginBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {}

    override fun initListener() {
        with(binding){
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }

}