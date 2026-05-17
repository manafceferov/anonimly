package com.anonimly.dto.user

open class UserRegisterDto @JvmOverloads constructor(
    open var username: String? = null,
    open var email: String? = null,
    open var password: String? = null
)