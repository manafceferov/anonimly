package com.anonimly.dto.post

open class PostResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var title: String? = null,
    open var slug: String? = null,
    open var coverImageUrl: String? = null,
    open var published: Boolean? = null,
    open var viewCount: Long? = null,
    open var authorUsername: String? = null,
    open var likeCount: Long? = null,
    open var dislikeCount: Long? = null,
    open var commentCount: Long? = null,
    open var createdAt: String? = null
)