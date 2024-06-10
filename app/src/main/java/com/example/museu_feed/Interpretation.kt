package com.example.museu_feed

data class Interpretation(
    val username: String = "",
    val text: String = "",
    val timestamp: String = "",
    val likes: Int,
    val isFlagged: Boolean
)