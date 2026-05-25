package com.anonimly.dto.comment

open class CommentResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var content: String? = null,
    open var authorUsername: String? = null,
    open var postId: Long? = null,
    open var postSlug: String? = null,
    open var parentId: Long? = null,
    open var replyCount: Int? = null,
    open var createdAt: String? = null,
    open var likeCount: Long? = null

)