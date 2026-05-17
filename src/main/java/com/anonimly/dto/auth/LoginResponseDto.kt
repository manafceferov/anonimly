package com.anonimly.dto.auth

import kotlin.jvm.JvmOverloads

open class LoginResponseDto @JvmOverloads constructor(
    open var token: String? = null,
    open var userId: Long? = null,
    open var username: String? = null,
    open var role: String? = null
)