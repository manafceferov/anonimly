package com.anonimly.entity

import com.anonimly.enums.ReactionType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "comment_reactions")
@CompoundIndex(name = "comment_user_idx", def = "{'commentId': 1, 'userId': 1}", unique = true)
data class CommentReaction(

    @Id
    var id: String? = null,

    var commentId: Long,

    var userId: Long,

    var type: ReactionType
)