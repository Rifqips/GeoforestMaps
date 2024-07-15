package id.application.geoforestmaps.presentation.feature.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.analytics.FirebaseAnalytics
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHomeBinding
import id.application.geoforestmaps.presentation.feature.account.AccountFragment
import id.application.geoforestmaps.presentation.feature.area.AreaFragment
import id.application.geoforestmaps.presentation.feature.history.HistoryFragment
import id.application.geoforestmaps.presentation.viewmodel.VmPreLogin
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, VmPreLogin>(FragmentHomeBinding::inflate) {

    override val viewModel: VmPreLogin by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun initView() {
        binding.bottomNavigation.background = null
        setUpFragment()
    }

    override fun initListener() {
        onBackPressed()
        with(binding){
            fabCamera.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
            }
        }

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
            .setMessage("Anda Yakin Ingin Keluar Aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                requireActivity().finishAffinity()
            }
            .setNegativeButton("Tidak", null)
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