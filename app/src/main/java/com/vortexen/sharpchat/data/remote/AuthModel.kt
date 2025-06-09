package com.vortexen.sharpchat.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SignUpMetaData(
    val phone: String = "",
    val full_name: String = "",
    val picture: String = "",
)