package com.example.museu_feed

data class Comment(
    val username: String,
    val text: String,
    val timestamp: String,
    val likes: Int
)
