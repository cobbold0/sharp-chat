package com.vortexen.sharpchat.repository

import androidx.paging.Pager
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.data.model.FriendRequest
import com.vortexen.sharpchat.data.model.UserSearchResult
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContactsSuggestionsPager(pageSize: Int = 20): Pager<String, ContactSuggestion>
    fun uploadContacts(): Flow<DataState<Int>>
    fun getContactSuggestions(): Flow<DataState<List<ContactSuggestion>>>
    fun sendFriendRequest(targetUserId: String): Flow<DataState<String>>
    fun respondToFriendRequest(requesterUserId: String, accept: Boolean): Flow<DataState<String>>
    fun getFriendRequests(): Flow<DataState<List<FriendRequest>>>
    fun searchUsers(searchTerm: String): Flow<DataState<List<UserSearchResult>>>
}