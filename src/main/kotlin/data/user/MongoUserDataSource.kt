package com.jwtdemo.data.user

import com.jwtdemo.data.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource (

    db: CoroutineDatabase,
) : UserDataSource {

    private val usersCollection = db.getCollection<User>("users")

    override suspend fun getUserByUsername(username: String): User? {
            return usersCollection.findOne(User::username eq username)
    }

    override suspend fun insertUser(user: User): Boolean {
        return usersCollection.insertOne(user).wasAcknowledged()
    }
}