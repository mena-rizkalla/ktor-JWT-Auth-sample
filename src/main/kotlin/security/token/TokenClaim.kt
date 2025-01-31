package com.jwtdemo.security.token

data class TokenClaim(
    val name: String,
    val value: String
)// to store info in a token
