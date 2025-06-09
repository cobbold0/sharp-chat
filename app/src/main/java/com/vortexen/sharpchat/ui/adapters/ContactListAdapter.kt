package com.vortexen.sharpchat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vortexen.sharpchat.R
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.databinding.LayoutContactListItemBinding
import timber.log.Timber

class ContactDiffCallback : DiffUtil.ItemCallback<ContactSuggestion>() {
    override fun areItemsTheSame(oldItem: ContactSuggestion, newItem: ContactSuggestion): Boolean {
        return oldItem.suggestion_id == newItem.suggestion_id
    }

    override fun areContentsTheSame(oldItem: ContactSuggestion, newItem: ContactSuggestion): Boolean {
        return oldItem == newItem
    }
}

class ContactViewHolder(private val binding: LayoutContactListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(contact: ContactSuggestion, onItemClick: (ContactSuggestion) -> Unit) {
        binding.name.text = contact.display_name
        binding.additionalInfo.text = contact.contact_name?.ifEmpty { contact.username }
//        binding.invite.visibility = if (contact.i.isNullOrEmpty()) View.VISIBLE else View.GONE

        try {
            Glide.with(itemView.context).load(R.drawable.bg_gradient) //TODO: Replace with actual image
                .placeholder(R.drawable.bg_gradient).circleCrop().into(binding.avatar)
        } catch (e: Exception) {
            Timber.e(e)
        }

        itemView.setOnClickListener {
            onItemClick(contact)
        }
    }
}


class ContactListAdapter(
    private val onItemClick: (ContactSuggestion) -> Unit = {}
) : PagingDataAdapter<ContactSuggestion, ContactViewHolder>(ContactDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            LayoutContactListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, onItemClick) }
    }
}