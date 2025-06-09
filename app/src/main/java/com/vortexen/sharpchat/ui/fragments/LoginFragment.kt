package com.vortexen.sharpchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.FragmentLoginBinding
import com.vortexen.sharpchat.ui.viewModels.SignInViewModel
import com.vortexen.sharpchat.utils.extensions.notify
import com.vortexen.sharpchat.utils.extensions.observeLiveDataState
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {
    @Inject
    lateinit var supabaseClient: SupabaseClient
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observers()
    }

    private fun setupUI() {
        binding.loginBtn.isEnabled = false
        binding.emailInput.addTextChangedListener {
            viewModel.onEmailChange(it.toString())
            enableLoginButton()
        }
        binding.passwordInput.addTextChangedListener {
            viewModel.onPasswordChange(it.toString())
            enableLoginButton()
        }
        binding.loginBtn.setOnClickListener {
            if (!validateFields()) {
                notify("Email and password are required")
            }
            viewModel.signInWithPassword()
        }
        binding.toSignUpBtn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment_to_signUpFragment)
        }
    }

    private fun validateFields(): Boolean {
        val isEmailReady = binding.emailInput.text.toString().isNotEmpty()
        val isPasswordReady = binding.passwordInput.text.toString().isNotEmpty()
        return isPasswordReady && isEmailReady
    }

    private fun enableLoginButton() {
        binding.loginBtn.isEnabled = validateFields()
    }

    private fun observers() {
        observeLiveDataState(viewModel.signInWithPasswordResult, true, onFailure = {
            if (it.isNotEmpty()) notify(it) else notify(getString(R.string.error_occurred))
        }) {
            notify("Logged in successfully")
            findNavController().navigate(R.id.loginFragment_to_mainActivity)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}