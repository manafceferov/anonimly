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
    open var likeCount: Long? = null,
    open var dislikeCount: Long? = 0,
    open var likedByMe: Boolean? = false,
    open var dislikedByMe: Boolean? = false,
    open var authorId: Long? = null,
    open var replies: List<CommentResponseDto>? = null

)