package com.example

import com.example.plugins.*
import com.mongodb.ConnectionString
import io.jsonwebtoken.Jwts
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

/*fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module)
        .start(wait = true)
}*/

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val database =
        KMongo.createClient(ConnectionString(Constants.MONGO_CONNECTION_LINK)).coroutine.getDatabase(Constants.DB_NAME)
    configureSecurity(database)
    configureHTTP()
    configureSerialization()
    configureRouting(database)

}
