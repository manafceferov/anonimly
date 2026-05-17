package com.anonimly.dto.post

open class PostDetailResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var title: String? = null,
    open var content: String? = null,
    open var slug: String? = null,
    open var coverImageUrl: String? = null,
    open var published: Boolean? = null,
    open var viewCount: Long? = null,
    open var authorUsername: String? = null,
    open var likeCount: Long? = null,
    open var dislikeCount: Long? = null,
    open var createdAt: String? = null,
    open var updatedAt: String? = null
)