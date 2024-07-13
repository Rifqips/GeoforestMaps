package id.application.geoforestmaps.presentation.feature.camera

import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.databinding.FragmentCameraBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment :
    BaseFragment<FragmentCameraBinding, VmPreLogin>(FragmentCameraBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {}

    override fun initListener() {}

}