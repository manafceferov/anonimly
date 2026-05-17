package com.anonimly.entity

import com.anonimly.enums.Role
import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User @JvmOverloads constructor(

    @Column(name = "username", nullable = false, unique = true, length = 50)
    open var username: String? = null,

    @Column(name = "email", nullable = false, unique = true, length = 100)
    open var email: String? = null,

    @Column(name = "password", nullable = false)
    open var password: String? = null,

    @Column(name = "bio", length = 500)
    open var bio: String? = null,

    @Column(name = "avatar_url")
    open var avatarUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    open var role: Role = Role.USER,

    @Column(name = "active", nullable = false)
    open var active: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var comments: MutableList<Comment> = mutableListOf()

) : BaseEntity()