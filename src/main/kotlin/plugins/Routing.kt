package com.jwtdemo.plugins

import com.jwtdemo.authenticate
import com.jwtdemo.data.user.UserDataSource
import com.jwtdemo.getSecretInfo
import com.jwtdemo.security.hashing.HashingService
import com.jwtdemo.security.token.TokenConfig
import com.jwtdemo.security.token.TokenService
import com.jwtdemo.signIn
import com.jwtdemo.signUp
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource,hashingService,tokenService,tokenConfig)
        signUp(hashingService,userDataSource)
        authenticate()
        getSecretInfo()
    }
}
