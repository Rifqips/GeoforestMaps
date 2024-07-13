package id.application.geoforestmaps.presentation.feature.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentMapsBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapsFragment :
    BaseFragment<FragmentMapsBinding, VmPreLogin>(FragmentMapsBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {}

    override fun initListener() {}

}