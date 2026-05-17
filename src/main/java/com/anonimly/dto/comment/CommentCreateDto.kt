package com.anonimly.dto.comment

open class CommentCreateDto @JvmOverloads constructor(
    open var content: String? = null,
    open var postId: Long? = null,
    open var parentId: Long? = null  // reply üçün
)