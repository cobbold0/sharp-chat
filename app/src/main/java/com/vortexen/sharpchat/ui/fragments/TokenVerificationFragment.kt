package com.vortexen.sharpchat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.data.constant.VerificationType
import com.vortexen.sharpchat.databinding.FragmentTokenVerificationBinding
import com.vortexen.sharpchat.ui.activities.MainActivity
import com.vortexen.sharpchat.ui.viewModels.SignUpViewModel
import com.vortexen.sharpchat.utils.extensions.notify
import com.vortexen.sharpchat.utils.extensions.observeLiveDataState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TokenVerificationFragment : Fragment() {
    private var _binding: FragmentTokenVerificationBinding? = null
    private val binding get() = _binding!!
    private val args: TokenVerificationFragmentArgs by navArgs()

    private val viewModel: SignUpViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTokenVerificationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.subtitle.text = if (args.verificationType == VerificationType.PHONE.toString()) {
            getString(R.string.we_sent_a_6_digit_code_to_your_phone)
        } else {
            getString(R.string.we_sent_a_6_digit_code_to_your_email)
        }
        binding.verifyButton.isEnabled = false
        setupTokenInputs()
        binding.verifyButton.setOnClickListener {
            val token =
                binding.token1.text.toString() + binding.token2.text.toString() + binding.token3.text.toString() + binding.token4.text.toString() + binding.token5.text.toString() + binding.token6.text.toString()

            if (!isTokenValid()) {
                notify(getString(R.string.error_occurred))
                return@setOnClickListener
            }

            viewModel.onOtpTokenChange(token)
            Timber.d("args.verificationType: ${args.verificationType}")
            if (args.verificationType == VerificationType.EMAIL.toString()) {
                viewModel.verifyCode(VerificationType.EMAIL)
            } else {
                viewModel.verifyCode(VerificationType.PHONE)
            }
        }
    }

    private fun setupTokenInputs() = binding.apply {
        val tokenInputs = listOf(token1, token2, token3, token4, token5, token6)

        tokenInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < tokenInputs.size - 1) {
                        tokenInputs[index + 1].requestFocus()
                    }
                    enableVerifyButton()
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editText.text.isEmpty() && index > 0) {
                    tokenInputs[index - 1].requestFocus()
                    true
                } else false
            }
        }
    }

    private fun isTokenValid(): Boolean {
        val token =
            binding.token1.text.toString() + binding.token2.text.toString() + binding.token3.text.toString() + binding.token4.text.toString() + binding.token5.text.toString() + binding.token6.text.toString()
        return token.length == 6
    }

    fun enableVerifyButton() {
        binding.verifyButton.isEnabled = isTokenValid()
    }

    private fun setupObservers() {
        observeLiveDataState(
            viewModel.verifyEmailResult, true, onFailure = { errorMessage ->
                val message = errorMessage.ifEmpty {
                    getString(R.string.error_occurred)
                }
                notify(message)
            }) { success ->
            if (success) {
                notify(getString(R.string.email_verified))
                navigateToMainActivity()
            }
        }

        observeLiveDataState(
            viewModel.verifyPhoneResult, true, onFailure = { errorMessage ->
                val message = errorMessage.ifEmpty {
                    getString(R.string.error_occurred)
                }
                notify(message)
            }) { success ->
            if (success) {
                notify(getString(R.string.phone_verified))
                navigateToMainActivity()
            }

        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}