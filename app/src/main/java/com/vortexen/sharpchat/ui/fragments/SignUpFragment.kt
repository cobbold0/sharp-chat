package com.vortexen.sharpchat.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.data.constant.VerificationType
import com.vortexen.sharpchat.databinding.FragmentSignUpBinding
import com.vortexen.sharpchat.ui.viewModels.SignUpViewModel
import com.vortexen.sharpchat.utils.extensions.notify
import com.vortexen.sharpchat.utils.extensions.observeLiveDataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() = binding.apply {
        continueBtn.isEnabled = false

        ccp.registerCarrierNumberEditText(editTextCarrierNumber)
        ccp.setPhoneNumberValidityChangeListener { isValidNumber ->
            if (isValidNumber) {
                viewModel.onPhoneNumberChange(binding.ccp.fullNumberWithPlus)
            }
            enableContinueButton()
        }
        ccp.setOnCountryChangeListener {
            enableContinueButton()
        }
        editTextCarrierNumber.addTextChangedListener {
            enableContinueButton()
        }

        nameInput.addTextChangedListener { editable ->
            val name = editable.toString()
            viewModel.onNameChange(name)
            validateName()
            enableContinueButton()
        }

        emailInput.addTextChangedListener { editable ->
            val email = editable.toString()
            viewModel.onEmailChange(email)
            validateEmail()
            enableContinueButton()
        }

        passwordInput.addTextChangedListener { editable ->
            val password = editable.toString()
            viewModel.onPasswordChange(password)
            validatePassword()
            enableContinueButton()
        }

        toLoginBtn.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment_to_loginFragment)
        }

        continueBtn.setOnClickListener {
            if (validateAllFields()) {
                viewModel.onSignUp()
            }
        }
    }


    private fun validateName(): Boolean {
        val name = binding.nameInput.text.toString().trim()
        return when {
            name.isEmpty() -> {
                binding.emailLayout.error = "Enter your name"
                false
            }
            else -> {
                binding.emailLayout.error = null
                true
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.emailInput.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.emailLayout.error = null
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.error = getString(R.string.invalid_email)
                false
            }
            else -> {
                binding.emailLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.passwordInput.text.toString()
        return when {
            password.isEmpty() -> {
                binding.passwordLayout.error = null
                false
            }
            password.length < MIN_PASSWORD_LENGTH -> {
                binding.passwordLayout.error = getString(R.string.password_too_short)
                false
            }
            !password.any { it.isDigit() } -> {
                binding.passwordLayout.error = getString(R.string.password_needs_number)
                false
            }
            !password.any { it.isUpperCase() } -> {
                binding.passwordLayout.error = getString(R.string.password_needs_uppercase)
                false
            }
            !password.any { it.isLowerCase() } -> {
                binding.passwordLayout.error = getString(R.string.password_needs_lowercase)
                false
            }
            else -> {
                binding.passwordLayout.error = null
                true
            }
        }
    }

    private fun validatePhoneNumber(): Boolean {
        val phone = binding.editTextCarrierNumber.text.toString()
       return when {
            (phone.isNotEmpty() && !binding.ccp.isValidFullNumber) -> {
                binding.editTextCarrierNumber.error = getString(R.string.invalid_phone_number)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validateAllFields(): Boolean {

        val emailValid = validateEmail()
        val passwordValid = validatePassword()
        val phoneValid = validatePhoneNumber()

        return emailValid && passwordValid && phoneValid
    }

    private fun enableContinueButton() {
        val nameIsValid = binding.nameInput.text.toString().isNotEmpty() && binding.nameLayout.error == null
        val emailIsValid = binding.emailInput.text.toString().isNotEmpty() &&
                binding.emailLayout.error == null
        val phoneIsValid = validatePhoneNumber()
        val passwordIsValid = binding.passwordInput.text.toString().isNotEmpty() &&
                binding.passwordLayout.error == null

        binding.continueBtn.isEnabled = nameIsValid && emailIsValid && phoneIsValid && passwordIsValid
    }

    private fun setupObservers() {
        observeLiveDataState(
            viewModel.signUpWithPasswordResult,
            true,
            onFailure = { errorMessage ->
                val message = errorMessage.ifEmpty {
                    getString(R.string.error_occurred)
                }
                notify(message)
            }
        ) { success ->
            if (success) {
                val action = SignUpFragmentDirections.signUpFragmentToTokenVerificationFragment(VerificationType.EMAIL.toString())
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }
}