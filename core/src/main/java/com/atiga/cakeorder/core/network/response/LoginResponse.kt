package com.atiga.cakeorder.core.network.response

class LoginResponse(
    var userId: String,
    var username: String,
    var token: String,
    var roleId: Int
)