package com.jwtdemo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val access_token: String
)
