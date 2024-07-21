package id.application.geoforestmaps.presentation.feature.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import id.application.core.utils.BaseFragment
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentHomeBinding
import id.application.geoforestmaps.presentation.feature.MainActivity
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, VmApplication>(FragmentHomeBinding::inflate) {

    override val viewModel: VmApplication by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun initView() {
        binding.bottomNavigation.background = null
        setUpFragment()
    }

    override fun initListener() {
        onBackPressed()
        with(binding){
            fabCamera.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_geotaggingLocationFragment)
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
                    R.id.menuDatabase -> containerBottomNavigation.findNavController()
                        .navigate(R.id.databaseFragment)
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
        (activity as? MainActivity)?.setLayoutLimits(false)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        firebaseAnalytics.logEvent(
            "home_page",
            Bundle().apply { putString("screenName", "HomePage") })
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.setLayoutLimits(true)
    }


}