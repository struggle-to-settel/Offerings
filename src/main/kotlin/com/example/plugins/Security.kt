package com.example.plugins

import com.example.responseModels.User
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.Constants
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.litote.kmongo.coroutine.CoroutineCollection

fun Application.configureSecurity(users: CoroutineCollection<User>) {
    install(Authentication) {
        jwt("accessToken") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Constants.JWT_SECRETE))
                    .build()
            )
            validate {
                val userId = it.payload.subject
                if (userId != null) {
                    val user = getUserById(users, userId)
                    if (user != null)
                        JWTPrincipal(it.payload)
                    else null
                } else null
            }
        }
    }
}

suspend fun getUserById(users: CoroutineCollection<User>, userId: String): User? {
    return users.findOneById(userId)
}

class UserSession(accessToken: String)
