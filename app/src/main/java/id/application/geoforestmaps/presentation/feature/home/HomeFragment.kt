package id.application.geoforestmaps.presentation.feature.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.analytics.FirebaseAnalytics
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHomeBinding
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, VmPreLogin>(FragmentHomeBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun initView() {
        setUpFragment()
    }

    override fun initListener() {
        onBackPressed()
    }

    private fun setUpFragment() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.container_bottom_navigation) as NavHostFragment
        val navController = navHostFragment.navController
        with(binding) {

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

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            }
        )
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                requireActivity().finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        firebaseAnalytics.logEvent(
            "home_page",
            Bundle().apply { putString("screenName", "HomePage") })
    }


}