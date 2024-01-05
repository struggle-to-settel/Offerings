package com.example

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.ktor.http.*
import java.util.*


fun generateJwtToken(username: String?): String {
    val expiration = Date(System.currentTimeMillis() + 3600000) // valid for one hour
    return Jwts.builder()
        .setSubject(username)
        .setExpiration(expiration)
        .signWith(SignatureAlgorithm.HS256, Constants.JWT_SECRETE.toByteArray())
        .compact()
}

class Res(val key: String, val value: Any?)

fun getMap(status: HttpStatusCode, message: String): Map<String, Any?> {
    return mapOf("status" to status.value, "message" to message)
}

fun getMap(status: HttpStatusCode, data: Any): Map<String, Any?> {
    return mapOf("status" to status.value, "data" to data)
}

fun getMap(status: HttpStatusCode, vararg res: Res): Map<String, Any?> {
    val map = hashMapOf<String, Any?>()
    map["status"] = status.value
    for (r in res)
        map[r.key] = r.value
    return map
}