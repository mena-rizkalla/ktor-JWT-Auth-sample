package com.jwtdemo

import com.jwtdemo.authenticate
import com.jwtdemo.data.model.User
import com.jwtdemo.data.requests.AuthRequest
import com.jwtdemo.data.requests.AuthResponse
import com.jwtdemo.data.user.UserDataSource
import com.jwtdemo.security.hashing.HashingService
import com.jwtdemo.security.hashing.SaltedHash
import com.jwtdemo.security.token.TokenClaim
import com.jwtdemo.security.token.TokenConfig
import com.jwtdemo.security.token.TokenService
import com.typesafe.config.ConfigException.Null
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post("signup") {
        val request = runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                null
            )
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if(areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict , null)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt,
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, null)
            return@post
        }
        call.respond(HttpStatusCode.OK, null)

    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){

    post("signin"){
        val request = runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest,
                null
            )
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if(user == null){
            call.respond(HttpStatusCode.Conflict, null)
            return@post
        }

        val isValidPassword = hashingService.verifySaltedHash(request.password,
            SaltedHash(user.password,user.salt)
        )

        if(!isValidPassword){
            call.respond(HttpStatusCode.Conflict, null)
        }

        val token = tokenService.generateToken(
            tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(token)
        )
    }
}

fun Route.authenticate(){
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo(){
    authenticate {
        get("secret"){
            val principle = call.principal<JWTPrincipal>()
            val userId = principle?.getClaim("userId", String::class)
            call.respondText("Secret info $userId", status = HttpStatusCode.OK)

        }
    }
}




