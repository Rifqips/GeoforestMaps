package id.application.geoforestmaps.presentation.feature.login

import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.window.layout.WindowMetricsCalculator
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentLoginBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment :
    BaseFragment<FragmentLoginBinding, VmPreLogin>(FragmentLoginBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {
        with(binding){
            val metrics = context?.let {
                WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(it)
            }
            val widthDp = metrics?.bounds?.width()?.div(resources.displayMetrics.density)
            if (widthDp != null) {
                when {
                    widthDp <= 320 -> {
                        Log.d("DeviceType", "Small phone")
                    }
                    widthDp <= 360 -> {
                        consBgLogin.maxHeight = (600 * resources.displayMetrics.density).toInt()
                        Log.d("DeviceType", "Medium phone")
                    }
                    else -> {
                        val layoutParams = consBgLogin.layoutParams
                        layoutParams.height = (750 * resources.displayMetrics.density).toInt()
                        consBgLogin.layoutParams = layoutParams
                        Log.d("DeviceType", "Large phone or tablet")
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