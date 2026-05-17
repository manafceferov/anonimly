package com.anonimly.entity

import jakarta.persistence.*

@Entity
@Table(name = "posts")
open class Post @JvmOverloads constructor(

    @Column(name = "title", nullable = false, length = 200)
    open var title: String? = null,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    open var content: String? = null,

    @Column(name = "slug", nullable = false, unique = true)
    open var slug: String? = null,

    @Column(name = "cover_image_url")
    open var coverImageUrl: String? = null,

    @Column(name = "published", nullable = false)
    open var published: Boolean = false,

    @Column(name = "view_count", nullable = false)
    open var viewCount: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var comments: MutableList<Comment> = mutableListOf()

) : BaseEntity()