package com.anonimly.dto.user

open class UserLoginDto @JvmOverloads constructor(
    open var email: String? = null,
    open var password: String? = null
)