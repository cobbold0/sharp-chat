package com.vortexen.sharpchat.data.model

import java.io.Serializable

data class Contact(
    val id: Long,
    val name: String,
    val email: String? = null,
    val phone: String,
    val photoUri: String? = null,
    val profileUrl: String? = null
) : Serializable


@kotlinx.serialization.Serializable
data class ContactInfo(
    val name: String, val phone: String
)

@kotlinx.serialization.Serializable
data class ContactSuggestion(
    val suggestion_id: String,
    val user_id: String,
    val username: String,
    val display_name: String?,
    val contact_name: String?,
    val source: String,
    val mutual_friends_count: Int
): Serializable

@kotlinx.serialization.Serializable
data class FriendRequest(
    val friendship_id: String,
    val requester_id: String,
    val username: String,
    val display_name: String?,
    val contact_name: String?,
    val created_at: String
)

@kotlinx.serialization.Serializable
data class UserSearchResult(
    val user_id: String, val username: String, val display_name: String?, val is_friend: Boolean
)