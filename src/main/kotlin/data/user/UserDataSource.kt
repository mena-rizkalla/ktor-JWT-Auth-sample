package com.jwtdemo.data.user

import com.jwtdemo.data.model.User

interface UserDataSource {

    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User) : Boolean

}