package com.anonimly.dto.user

open class UserResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var username: String? = null,
    open var email: String? = null,
    open var bio: String? = null,
    open var avatarUrl: String? = null,
    open var role: String? = null
)