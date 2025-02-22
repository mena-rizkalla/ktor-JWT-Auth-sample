package com.jwtdemo.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService : TokenService {
    override fun generateToken(config: TokenConfig, vararg claim: TokenClaim): String {
        var token =  JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn * 1000))

        claim.forEach {
            token = token.withClaim(it.name, it.value)
        }

        return token.sign(Algorithm.HMAC256(config.secretKey))
    }


}