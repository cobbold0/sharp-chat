package com.vortexen.sharpchat.repository.impl

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.model.Contact
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.data.model.FriendRequest
import com.vortexen.sharpchat.data.model.UserSearchResult
import com.vortexen.sharpchat.data.paging.ContactPagingSource
import com.vortexen.sharpchat.repository.ContactRepository
import com.vortexen.sharpchat.services.ContactDiscoveryService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val application: Application,
    private val contactDiscoveryService: ContactDiscoveryService
) : ContactRepository {
    override fun getContactsSuggestionsPager(pageSize: Int): Pager<String, ContactSuggestion> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize, enablePlaceholders = false
            ), pagingSourceFactory = {
                ContactPagingSource(contactDiscoveryService, pageSize)
            }
        )
    }

    override fun uploadContacts(): Flow<DataState<Int>> {
        return contactDiscoveryService.uploadContacts(application)
    }

    override fun getContactSuggestions(): Flow<DataState<List<ContactSuggestion>>> {
        return contactDiscoveryService.getContactSuggestions()
    }

    override fun sendFriendRequest(targetUserId: String): Flow<DataState<String>> {
        return contactDiscoveryService.sendFriendRequest(targetUserId)
    }

    override fun respondToFriendRequest(
        requesterUserId: String, accept: Boolean
    ): Flow<DataState<String>> {
        return contactDiscoveryService.respondToFriendRequest(requesterUserId, accept)
    }

    override fun getFriendRequests(): Flow<DataState<List<FriendRequest>>> {
        return contactDiscoveryService.getFriendRequests()
    }

    override fun searchUsers(searchTerm: String): Flow<DataState<List<UserSearchResult>>> {
        return contactDiscoveryService.searchUsers(searchTerm)
    }


}
