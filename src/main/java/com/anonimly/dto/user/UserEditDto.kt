package com.anonimly.dto.user

open class UserEditDto @JvmOverloads constructor(
    open var username: String? = null,
    open var bio: String? = null,
    open var avatarUrl: String? = null
)