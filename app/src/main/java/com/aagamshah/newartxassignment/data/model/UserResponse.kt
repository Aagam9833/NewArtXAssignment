package com.aagamshah.newartxassignment.data.model

import com.aagamshah.newartxassignment.domain.model.User


data class UserResponse(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class UserDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        image = image
    )
}