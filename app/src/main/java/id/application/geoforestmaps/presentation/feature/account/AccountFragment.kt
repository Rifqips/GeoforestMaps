package id.application.geoforestmaps.presentation.feature.account

import android.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import id.application.core.utils.BaseFragment
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentAccountBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment :
    BaseFragment<FragmentAccountBinding, VmApplication>(FragmentAccountBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        observeVM()
    }

    override fun initListener() {
        with(binding){
            btnLogout.setOnClickListener {
                showExitConfirmationDialog()
            }
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Anda Yakin Ingin Keluar Aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                viewModel.userLogout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun observeVM() {
        with(viewModel){
            getUserName()
            getUserEmail()
            viewModel.isUserName.observe(viewLifecycleOwner){
                with(binding){
                    tvNameAccount.text =  it
                    it.let { name ->
                        if (name.isNotEmpty()) {
                            tvUserIcon.text = name[0].toString()
                        } else {
                            tvUserIcon.text = ""
                        }
                    }
                }
            }
            viewModel.isUserEmail.observe(viewLifecycleOwner){ email->
                binding.tvEmailAccount.text = email
            }
            logoutResults.observe(viewLifecycleOwner) { result ->
                result.proceedWhen(
                    doOnSuccess = {
                        StyleableToast.makeText(
                            requireContext(),
                            getString(R.string.text_successfully_logout),
                            R.style.successtoast
                        ).show()
                        performLogout()
                    },
                    doOnError = {
                        StyleableToast.makeText(
                            requireContext(),
                            getString(R.string.text_failed_to_logout),
                            R.style.failedtoast
                        ).show()
                    }
                )
            }
        }
    }

    private fun performLogout() {
        activity?.supportFragmentManager?.
        findFragmentById(R.id.container_navigation)
            ?.findNavController()?.navigate(R.id.action_homeFragment_to_loginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .setLaunchSingleTop(true)
                    .build())
    }
}