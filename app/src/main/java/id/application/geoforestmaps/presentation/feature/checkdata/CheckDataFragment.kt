package id.application.geoforestmaps.presentation.feature.checkdata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentCheckDataBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckDataFragment :
    BaseFragment<FragmentCheckDataBinding, VmApplication>(FragmentCheckDataBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        with(binding){
            topBar.ivTitle.text = "Check Data"
        }
    }

    override fun initListener() {}

}