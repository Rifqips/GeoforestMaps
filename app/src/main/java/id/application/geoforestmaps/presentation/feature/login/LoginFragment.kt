package id.application.geoforestmaps.presentation.feature.login

import android.util.Patterns
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.window.layout.WindowMetricsCalculator
import id.application.core.domain.model.login.UserLoginRequest
import id.application.core.utils.BaseFragment
import id.application.core.utils.exceptions.ApiException
import id.application.core.utils.exceptions.NoInternetException
import id.application.core.utils.proceedWhen
import id.application.geoforestmaps.R
import id.application.geoforestmaps.databinding.FragmentLoginBinding
import id.application.geoforestmaps.presentation.viewmodel.VmApplication
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment :
    BaseFragment<FragmentLoginBinding, VmApplication>(FragmentLoginBinding::inflate) {

    override val viewModel: VmApplication by viewModel()

    override fun initView() {
        with(binding) {
            val metrics = context?.let {
                WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(it)
            }
            val widthDp = metrics?.bounds?.width()?.div(resources.displayMetrics.density)
            if (widthDp != null) {
                when {
                    widthDp <= 360 -> {
                        consBgLogin.maxHeight = (600 * resources.displayMetrics.density).toInt()
                    }

                    else -> {
                        val layoutParams = consBgLogin.layoutParams
                        layoutParams.height = (750 * resources.displayMetrics.density).toInt()
                        consBgLogin.layoutParams = layoutParams
                    }
                }
            }
        }

        observeResult()
    }

    override fun initListener() {
        with(binding) {
            btnLogin.setOnClickListener {
                doLogin()
            }
        }
    }

    private fun observeResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    StyleableToast.makeText(
                        requireContext(),
                        getString(R.string.text_login_successful),
                        R.style.successtoast
                    ).show()
                    it.payload?.let {
                        // save user id
                    }
                    navigateToDashboard()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnLogin.isVisible = false
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnLogin.isVisible = true
                    binding.btnLogin.isEnabled = true

                    if ((it.exception as ApiException).httpCode == 500) {
                        StyleableToast.makeText(
                            requireContext(),
                            getString(R.string.text_sorry_there_s_an_error_on_the_server),
                            R.style.failedtoast
                        ).show()

                    } else if (it.exception is NoInternetException) {
                        if (!(it.exception as NoInternetException).isNetworkAvailable(requireContext())) {
                            StyleableToast.makeText(
                                requireContext(),
                                getString(R.string.text_no_internet_connection),
                                R.style.failedtoast
                            ).show()
                        }
                    }
                }
            )
        }
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun doLogin() {
        if (isFormValid()) {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val userAuth = UserLoginRequest(
                email,
                password
            )
            viewModel.userLogin(userAuth)
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        return checkEmailValidation(email) &&
                checkPasswordValidation(password)
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.tilEmail.isErrorEnabled = true
            binding.tilEmail.error = getString(R.string.text_error_email_empty)
            binding.etEmail.setBackgroundResource(R.drawable.bg_edit_text_error)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.isErrorEnabled = true
            binding.tilEmail.error = getString(R.string.text_error_email_invalid)
            binding.etEmail.setBackgroundResource(R.drawable.bg_edit_text_error)
            false
        } else {
            binding.tilEmail.isErrorEnabled = false
            true
        }
    }

    private fun checkPasswordValidation(password: String): Boolean {
        return if (password.isEmpty()) {
            binding.tilPassword.isErrorEnabled = true
            binding.tilPassword.error = getString(R.string.text_sorry_wrong_password)
            binding.etPassword.setBackgroundResource(R.drawable.bg_edit_text_error)
            false
        } else if (password.length < 8) {
            binding.tilPassword.isErrorEnabled = true
            binding.tilPassword.error = getString(R.string.text_password_min_8_character)
            binding.etPassword.setBackgroundResource(R.drawable.bg_edit_text_error)
            false
        } else {
            binding.tilPassword.isErrorEnabled = false
            true
        }
    }


}