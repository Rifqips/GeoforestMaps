package id.application.geoforestmaps.presentation.feature.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import id.application.geoforestmaps.databinding.DialogConfirmCustomBinding
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
                    showDialog()
                }
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showDialog() {
        val binding: DialogConfirmCustomBinding =
            DialogConfirmCustomBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), 0).create()

        dialog.apply {
            setView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
        }.show()
        with(binding){
            dialogTitle.text = "Anda Yakin Ingin Keluar\nAplikasi?"
            btnYes.setOnClickListener {
                requireActivity().finishAffinity()
                dialog.dismiss()
            }
            btnNo.setOnClickListener {
                dialog.dismiss()
            }
            root.setOnTouchListener { _, _ ->
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        firebaseAnalytics.logEvent(
            "home_page",
            Bundle().apply { putString("screenName", "HomePage") })
    }

    override fun onPause() {
        super.onPause()
    }
}