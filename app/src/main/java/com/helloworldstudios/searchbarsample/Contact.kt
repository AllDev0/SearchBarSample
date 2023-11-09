package com.helloworldstudios.searchbarsample

data class Contact(
    val firstName: String,
    val middleName: String? = "",
    val lastName: String,
    val phoneNumber: String
)
