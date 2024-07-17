package id.application.geoforestmaps.presentation.feature.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentSplashBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment :
    BaseFragment<FragmentSplashBinding, VmApplication>(FragmentSplashBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        lifecycleScope.launch {
            delay(3000)
            checkLoginResult()
        }
    }

    override fun initListener() {}

    private fun checkLoginResult() {
        viewModel.checkLogin()
        viewModel.isUserLogin.observe(viewLifecycleOwner) { isLogin ->
            if (!isLogin) {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }else{
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }
        }
    }

}