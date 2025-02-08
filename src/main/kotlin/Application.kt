package com.jwtdemo

import com.auth0.jwt.algorithms.Algorithm
import com.jwtdemo.data.model.User
import com.jwtdemo.data.user.MongoUserDataSource
import com.jwtdemo.plugins.configureMonitoring
import com.jwtdemo.plugins.configureRouting
import com.jwtdemo.plugins.configureSecurity
import com.jwtdemo.plugins.configureSerialization
import com.jwtdemo.security.hashing.SHA256HashingService
import com.jwtdemo.security.token.JwtTokenService
import com.jwtdemo.security.token.TokenConfig
import io.ktor.server.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val mongoPW = System.getenv("MONGO_PW")
    val dbName = "ktor-auth"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://menarizkalla:$mongoPW@cluster0.z5v6h.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
    ).coroutine
        .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        secretKey = System.getenv("JWT_SECRET"),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
    )
    val hashingService = SHA256HashingService()



    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService , tokenService , tokenConfig)

}
