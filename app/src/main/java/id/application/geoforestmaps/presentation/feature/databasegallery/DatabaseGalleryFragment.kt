package id.application.geoforestmaps.presentation.feature.databasegallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseGalleryBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseGalleryFragment :
    BaseFragment<FragmentDatabaseGalleryBinding, VmApplication>(FragmentDatabaseGalleryBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {}

    override fun initListener() {}

}