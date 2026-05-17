package com.anonimly.dto.post

open class PostEditDto @JvmOverloads constructor(
    open var title: String? = null,
    open var content: String? = null,
    open var coverImageUrl: String? = null,
    open var published: Boolean? = null
)