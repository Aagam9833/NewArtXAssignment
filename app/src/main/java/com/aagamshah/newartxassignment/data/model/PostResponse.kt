package com.aagamshah.newartxassignment.data.model

data class PostResponse(
    val posts: List<PostDto>
)

data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val tags: List<String>,
    val reactions: ReactionsDto
)

data class ReactionsDto(
    val likes: Int,
    val dislikes: Int
)