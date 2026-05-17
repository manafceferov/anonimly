package com.anonimly.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.CompoundIndex
import java.time.LocalDateTime

@Document(collection = "likes")
@CompoundIndex(name = "user_post_idx", def = "{'userId': 1, 'postId': 1}", unique = true)
open class Like @JvmOverloads constructor(

    @Id
    open var id: String? = null,

    open var postId: Long? = null,

    open var userId: Long? = null,

    open var type: String? = null,

    open var createdAt: LocalDateTime? = LocalDateTime.now()
)