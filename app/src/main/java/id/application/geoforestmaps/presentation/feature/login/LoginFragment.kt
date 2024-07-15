package id.application.geoforestmaps.presentation.feature.login

import androidx.navigation.fragment.findNavController
import androidx.window.layout.WindowMetricsCalculator
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentLoginBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment :
    BaseFragment<FragmentLoginBinding, VmApplication>(FragmentLoginBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        with(binding){
            val metrics = context?.let {
                WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(it)
            }
            val widthDp = metrics?.bounds?.width()?.div(resources.displayMetrics.density)
            if (widthDp != null) {
                when {
                    widthDp <= 360 -> {
                        consBgLogin.maxHeight = (600 * resources.displayMetrics.density).toInt()
                    }
                    else -> {
                        val layoutParams = consBgLogin.layoutParams
                        layoutParams.height = (750 * resources.displayMetrics.density).toInt()
                        consBgLogin.layoutParams = layoutParams
                    }
                }
            }
        }

    }

    override fun initListener() {
        with(binding){
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
    }

}