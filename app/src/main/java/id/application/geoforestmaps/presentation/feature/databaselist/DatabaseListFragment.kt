package id.application.geoforestmaps.presentation.feature.databaselist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentDatabaseListBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class DatabaseListFragment : BaseFragment<FragmentDatabaseListBinding, VmApplication>(FragmentDatabaseListBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {}

    override fun initListener() {}

}