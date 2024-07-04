package id.application.core.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<b : ViewBinding, vm : ViewModel>(
    val bindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> b
) : Fragment(){

    protected abstract val viewModel :vm
    protected lateinit var binding: b

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingFactory(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    abstract fun initView()

    abstract fun initListener()
}