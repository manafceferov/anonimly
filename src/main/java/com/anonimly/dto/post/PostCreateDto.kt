package com.anonimly.dto.post

open class PostCreateDto @JvmOverloads constructor(
    open var title: String? = null,
    open var content: String? = null,
    open var coverImageUrl: String? = null,
    open var published: Boolean = false
)