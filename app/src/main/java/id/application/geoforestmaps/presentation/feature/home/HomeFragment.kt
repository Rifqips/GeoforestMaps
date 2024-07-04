package id.application.geoforestmaps.presentation.feature.home

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHomeBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, VmPreLogin>(FragmentHomeBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()

    override fun initView() {
        setUpFragment()
    }

    override fun initListener() {}

    private fun setUpFragment() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.container_bottom_navigation) as NavHostFragment
        val navController = navHostFragment.navController
        with(binding){

            val bottomNav = binding.bottomNavigation as BottomNavigationView

            bottomNav.setupWithNavController(navController)
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menuDashboard -> containerBottomNavigation.findNavController()
                        .navigate(R.id.dashboardFragment)

                    R.id.menuHistory -> containerBottomNavigation.findNavController()
                        .navigate(R.id.historyFragment)

                    R.id.menuArea -> containerBottomNavigation.findNavController()
                        .navigate(R.id.areaFragment)

                    R.id.menuAccount -> containerBottomNavigation.findNavController()
                        .navigate(R.id.accountFragment)
                }
                true
            }
        }
    }

}