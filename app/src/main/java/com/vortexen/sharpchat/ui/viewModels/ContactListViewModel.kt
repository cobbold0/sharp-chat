package com.vortexen.sharpchat.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.model.Contact
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.data.model.FriendRequest
import com.vortexen.sharpchat.data.model.UserSearchResult
import com.vortexen.sharpchat.repository.ContactRepository
import com.vortexen.sharpchat.utils.BaseViewModel
import com.vortexen.sharpchat.utils.extensions.emitFlowResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : BaseViewModel() {
    val contacts: Flow<PagingData<ContactSuggestion>> =
        contactRepository.getContactsSuggestionsPager().flow.cachedIn(viewModelScope)

    private val _getContactSuggestions = MutableLiveData<DataState<List<ContactSuggestion>>>()
    val getContactSuggestions: MutableLiveData<DataState<List<ContactSuggestion>>> =
        _getContactSuggestions
    fun getContactSuggestions() = emitFlowResults(_getContactSuggestions) {
        contactRepository.getContactSuggestions()
    }

    private val _uploadContacts = MutableLiveData<DataState<Int>>()
    val uploadContacts: MutableLiveData<DataState<Int>> = _uploadContacts
    fun uploadContacts() = emitFlowResults(_uploadContacts) {
        contactRepository.uploadContacts()
    }

    private val _sendFriendRequest = MutableLiveData<DataState<String>>()
    val sendFriendRequest: MutableLiveData<DataState<String>> = _sendFriendRequest
    fun sendFriendRequest(targetUserId: String) = emitFlowResults(_sendFriendRequest) {
        contactRepository.sendFriendRequest(targetUserId)
    }

    private val _respondToFriendRequest = MutableLiveData<DataState<String>>()
    val respondToFriendRequest: MutableLiveData<DataState<String>> = _respondToFriendRequest
    fun respondToFriendRequest(requesterUserId: String, accept: Boolean) =
        emitFlowResults(_respondToFriendRequest) {
            contactRepository.respondToFriendRequest(requesterUserId, accept)
        }

    private val _getFriendRequests = MutableLiveData<DataState<List<FriendRequest>>>()
    val getFriendRequests: MutableLiveData<DataState<List<FriendRequest>>> = _getFriendRequests
    fun getFriendRequests() = emitFlowResults(_getFriendRequests) {
        contactRepository.getFriendRequests()
    }

    private val _searchUsers = MutableLiveData<DataState<List<UserSearchResult>>>()
    val searchUsers: MutableLiveData<DataState<List<UserSearchResult>>> = _searchUsers
    fun searchUsers(searchTerm: String) = emitFlowResults(_searchUsers) {
        contactRepository.searchUsers(searchTerm)
    }

}
