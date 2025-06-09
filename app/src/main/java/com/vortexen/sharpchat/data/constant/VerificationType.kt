package com.vortexen.sharpchat.data.constant

enum class VerificationType(private val stringValue: String) {
    EMAIL("Email"), PHONE("Phone");

    override fun toString(): String {
        return stringValue
    }
}