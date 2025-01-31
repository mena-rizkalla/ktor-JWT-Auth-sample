package com.jwtdemo.security.token

// how token generation logic works
interface TokenService {

    fun generateToken(
        config: TokenConfig,
        vararg claim: TokenClaim
    ): String

}
