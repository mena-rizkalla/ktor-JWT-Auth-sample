package com.jwtdemo

import com.jwtdemo.data.model.User
import com.jwtdemo.data.requests.AuthRequest
import com.jwtdemo.data.requests.AuthResponse
import com.jwtdemo.data.user.UserDataSource
import com.jwtdemo.security.hashing.HashingService
import com.typesafe.config.ConfigException.Null
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post("singup") {
        val request = kotlin.runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
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


