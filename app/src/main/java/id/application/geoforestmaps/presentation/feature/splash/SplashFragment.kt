package id.application.geoforestmaps.presentation.feature.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentSplashBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment :
    BaseFragment<FragmentSplashBinding, VmPreLogin>(FragmentSplashBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {}

    override fun initListener() {
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

}