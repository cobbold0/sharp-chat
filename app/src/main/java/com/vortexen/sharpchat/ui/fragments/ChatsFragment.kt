package com.vortexen.sharpchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.FragmentChatsBinding
import com.vortexen.sharpchat.utils.PermissionUtils
import com.vortexen.sharpchat.utils.extensions.notify
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {

    @Inject
    lateinit var supabaseClient: SupabaseClient
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                val permission = entry.key
                val isGranted = entry.value
                if (isGranted) {
                    // Permission granted
                } else {
                    notify("Permission denied: $permission")
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        checkAndRequestPermissions()
    }

    private fun setupUI() {
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.chatsFragment_to_contactListFragment)
        }
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = PermissionUtils.getRequiredPermissions()

        val missingPermissions = requiredPermissions.filter { permission ->
            !PermissionUtils.isPermissionGranted(requireContext(), permission)
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissionsLauncher.launch(
                missingPermissions.toTypedArray()
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}