package com.jwtdemo

import com.jwtdemo.data.model.User
import com.jwtdemo.data.user.MongoUserDataSource
import com.jwtdemo.plugins.configureMonitoring
import com.jwtdemo.plugins.configureRouting
import com.jwtdemo.plugins.configureSecurity
import com.jwtdemo.plugins.configureSerialization
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



    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
