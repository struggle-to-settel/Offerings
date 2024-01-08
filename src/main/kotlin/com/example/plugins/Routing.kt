package com.example.plugins

import com.example.Constants
import com.example.responseModels.User
import com.example.Res
import com.example.generateJwtToken
import com.example.getMap
import com.mongodb.client.model.UpdateOptions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

fun Application.configureRouting(database: CoroutineDatabase) {
    routing {

        val users = database.getCollection<User>(Constants.USERS)

        get("/") {
            call.respond(getMap(HttpStatusCode.OK, "Hello There!"))
        }

        userModule(users)
        authModule(users)
    }
}

fun Routing.authModule(users: CoroutineCollection<User>) {

    // CREATE a user
    post("/auth/signUp") {
        val user = call.receive<User>()
        users.insertOne(user)
        call.respond(getMap(HttpStatusCode.Created, Res("id", user._id)))
    }

    // Login a user
    post("/auth/login") {
        val creds = call.receive<User>()
        val user = users.find(User::username eq creds.username, User::password eq creds.password).first()
        if (user != null) {
            val token = generateJwtToken(user.username)
            user.token = token
            call.respond(getMap(HttpStatusCode.OK, user))
            users.updateOneById(user._id ?: "", user)
        } else {
            call.respond(getMap(HttpStatusCode.BadRequest, "username or password is incorrect"))
        }
    }

}

fun Routing.userModule(users: CoroutineCollection<User>) {

    authenticate("accessToken") {

        // GET particular user
        get("/users/{_id}") {
            val userId = call.parameters["_id"] ?: return@get call.respond(
                getMap(HttpStatusCode.BadRequest, "com.example.responseModels.User Id required!")
            )
            val user = users.findOneById(userId) ?: return@get call.respond(
                getMap(HttpStatusCode.NotFound, "com.example.responseModels.User not found!")
            )
            call.respond(getMap(HttpStatusCode.OK, user))
        }

        // UPDATE existing user
        put("/users/{id}") {
            val userId = call.parameters["id"] ?: return@put call.respond(
                getMap(
                    HttpStatusCode.BadRequest,
                    "User Id is required"
                )
            )
            val user = call.receive<User>()
            users.updateOneById(userId, user, updateOnlyNotNullProperties = true)
            call.respond(getMap(HttpStatusCode.OK, "Updated Successfully"))
        }

        // DELETE a user
        delete("/users/{_id}") {
            val userId = call.parameters["_id"] ?: return@delete call.respond(
                getMap(
                    HttpStatusCode.BadRequest,
                    "User Id is required!"
                )
            )
            users.deleteOneById(userId)
            call.respond(getMap(HttpStatusCode.OK, "User deleted successfully"))
        }

    }

}

