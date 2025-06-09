package com.vortexen.sharpchat.services

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.vortexen.sharpchat.data.DataState
import com.vortexen.sharpchat.data.model.ContactInfo
import com.vortexen.sharpchat.data.model.ContactSuggestion
import com.vortexen.sharpchat.data.model.FriendRequest
import com.vortexen.sharpchat.data.model.UserSearchResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import timber.log.Timber
import javax.inject.Inject


class ContactDiscoveryService @Inject constructor(private val supabase: SupabaseClient, private val application: Application) {

    /**
     * Read contacts from phone and upload to Supabase
     */
    fun uploadContacts(context: Context): Flow<DataState<Int>> {
        return flow {
            emit(DataState.Loading)
            try {
                val contacts = readPhoneContacts(context)
                val contactsJson = buildJsonArray(contacts)

                val response = supabase.postgrest.rpc(
                    function = "upload_user_contacts", parameters = buildJsonObject {
                        put("contacts", contactsJson)
                    })

                val result = response.decodeAs<JsonObject>()
                val matchedCount = result["matched_count"]?.toString()?.toIntOrNull() ?: 0

                Timber.d("Matched count: $matchedCount")
                Timber.d("Result: $result")

                emit(DataState.Success(matchedCount))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    /**
     * Get contact suggestions (people in contacts who are on the app)
     */
    fun getContactSuggestions(limit: Int = 20): Flow<DataState<List<ContactSuggestion>>> {
        return flow {
            emit(DataState.Loading)
            try {
                val response = supabase.postgrest.rpc(
                    function = "get_contact_suggestions", parameters = buildJsonObject {
                        put("limit_count", limit)
                    })

                val suggestions = response.decodeAs<List<ContactSuggestion>>()
                emit(DataState.Success(suggestions))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }


    /**
     * Send friend request
     */
    fun sendFriendRequest(targetUserId: String): Flow<DataState<String>> {
        return flow {
            emit(DataState.Loading)
            try {
                val response = supabase.postgrest.rpc(
                    function = "send_friend_request", parameters = buildJsonObject {
                        put("target_user_id", targetUserId)
                    })

                val result = response.decodeAs<JsonObject>()
                if (result.containsKey("error")) {
                    emit(DataState.Error(Exception(result["error"].toString())))
                } else {
                    emit(DataState.Success("Friend request sent"))
                }
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    /**
     * Respond to friend request
     */
    fun respondToFriendRequest(
        requesterUserId: String, accept: Boolean
    ): Flow<DataState<String>> {
        return flow {
            emit(DataState.Loading)
            try {
                val response = supabase.postgrest.rpc(
                    function = "respond_to_friend_request", parameters = buildJsonObject {
                        put("requester_user_id", requesterUserId)
                        put("response", if (accept) "accepted" else "declined")
                    })

                val result = response.decodeAs<JsonObject>()
                if (result.containsKey("error")) {
                    emit(DataState.Error(Exception(result["error"].toString())))
                } else {
                    emit(DataState.Success(if (accept) "Request accepted" else "Request declined"))
                }
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    /**
     * Get incoming friend requests
     */
    fun getFriendRequests(): Flow<DataState<List<FriendRequest>>> {
        return flow {
            emit(DataState.Loading)
            try {
                val response = supabase.postgrest.rpc("get_friend_requests")
                val requests = response.decodeAs<List<FriendRequest>>()
                emit(DataState.Success(requests))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    /**
     * Search users by username
     */
    fun searchUsers(searchTerm: String): Flow<DataState<List<UserSearchResult>>> {
        return flow {
            emit(DataState.Loading)
            try {
                val response = supabase.postgrest.rpc(
                    function = "search_users_by_username", parameters = buildJsonObject {
                        put("search_term", searchTerm)
                    })

                val users = response.decodeAs<List<UserSearchResult>>()
                emit(DataState.Success(users))
            } catch (e: Exception) {
                Timber.e(e)
                emit(DataState.Error(Exception(e.localizedMessage)))
            }
        }
    }

    /**
     * Read contacts from phone (requires READ_CONTACTS permission)
     */
    private fun readPhoneContacts(context: Context): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ), null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Unknown"
                val phone = it.getString(phoneIndex)?.replace(Regex("[^+0-9]"), "") ?: ""

                if (phone.isNotEmpty()) {
                    contacts.add(ContactInfo(name, phone))
                }
            }
        }

        return contacts.distinctBy { it.phone } // Remove duplicates
    }

    private fun buildJsonArray(contacts: List<ContactInfo>): JsonArray {
        return kotlinx.serialization.json.buildJsonArray {
            contacts.forEach { contact ->
                add(buildJsonObject {
                    put("name", contact.name)
                    put("phone", contact.phone)
                })
            }
        }
    }
}