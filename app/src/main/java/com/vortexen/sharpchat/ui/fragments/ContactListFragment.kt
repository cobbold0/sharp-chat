package com.vortexen.sharpchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.databinding.FragmentContactListBinding
import com.vortexen.sharpchat.ui.adapters.ContactListAdapter
import com.vortexen.sharpchat.ui.viewModels.ContactListViewModel
import com.vortexen.sharpchat.utils.PermissionUtils
import com.vortexen.sharpchat.utils.VerticalSpaceItemDecoration
import com.vortexen.sharpchat.utils.extensions.navigateBack
import com.vortexen.sharpchat.utils.extensions.notify
import com.vortexen.sharpchat.utils.extensions.observeLiveDataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactListViewModel by viewModels()

    private val adapter: ContactListAdapter by lazy {
        ContactListAdapter(onItemClick = { contact ->
            val action = ContactListFragmentDirections.contactListFragmentToChatFragment(contact)
            findNavController().navigate(action)
        })
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                val permission = entry.key
                val isGranted = entry.value
                if (isGranted) {
                    viewModel.uploadContacts()
                    lifecycleScope.launchWhenStarted {
                        viewModel.contacts.collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                    }
                } else {
                    notify("Permission denied: $permission")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        checkAndRequestPermissions()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateBack()
        }

        observeLiveDataState(viewModel.uploadContacts, true, onFailure = {
            notify(it.ifEmpty { "Error uploading contacts" })
        }) {
            notify("Contacts uploaded successfully")
        }
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = PermissionUtils.getRequiredPermissions()

        val missingPermissions = requiredPermissions.filter { permission ->
            !PermissionUtils.isPermissionGranted(requireContext(), permission)
        }

        val isContactsPermissionGranted = PermissionUtils.isPermissionGranted(requireContext(), android.Manifest.permission.READ_CONTACTS)

        if (!isContactsPermissionGranted) {
            requestPermissionsLauncher.launch(
                missingPermissions.toTypedArray()
            )
        } else {
            viewModel.uploadContacts()
            lifecycleScope.launchWhenStarted {
                viewModel.contacts.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun setupUI() {

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        binding.actionBar.setOnBackClickListener {
            navigateBack()
        }

        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    showLoadingState()
                }

                is LoadState.NotLoading -> {
                    showSuccessState()
                }

                is LoadState.Error -> {
                    showErrorState("Error loading data")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.contactsRv.adapter = adapter
        binding.contactsRv.layoutManager = LinearLayoutManager(requireContext())

        val spacing = resources.getDimensionPixelSize(R.dimen.contact_item_spacing)
        binding.contactsRv.addItemDecoration(VerticalSpaceItemDecoration(spacing))
    }

    private fun showLoadingState() = binding.apply {
        swipeRefreshLayout.isRefreshing = true
        errorStateView.errorStateView.visibility = View.GONE
        emptyStateView.emptyStateView.visibility = View.GONE
        contactsRv.visibility = View.VISIBLE
    }


    private fun showSuccessState() = binding.apply {
        swipeRefreshLayout.isRefreshing = false
        errorStateView.errorStateView.visibility = View.GONE
        emptyStateView.emptyStateView.visibility = View.GONE
        contactsRv.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) = binding.apply {
        swipeRefreshLayout.isRefreshing = false
        contactsRv.visibility = View.GONE
        emptyStateView.emptyStateView.visibility = View.GONE
        errorStateView.errorStateView.visibility = View.VISIBLE
        notify(message)
    }

    private fun showEmptyState() = binding.apply {
        swipeRefreshLayout.isRefreshing = false
        contactsRv.visibility = View.GONE
        errorStateView.errorStateView.visibility = View.GONE
        emptyStateView.emptyStateView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}