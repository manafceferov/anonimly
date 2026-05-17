package com.anonimly.entity

import jakarta.persistence.*

@Entity
@Table(name = "comments")
open class Comment @JvmOverloads constructor(

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    open var content: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    open var post: Post? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    open var parent: Comment? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var replies: MutableList<Comment> = mutableListOf()

) : BaseEntity()