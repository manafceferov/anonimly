package com.anonimly.dto.auth

open class LoginRequestDto @JvmOverloads constructor(
    open var username: String? = null,
    open var password: String? = null
)