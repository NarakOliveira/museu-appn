package com.example.museu_feed

data class ArtworkEntry(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val comments: List<Map<String, Any>> = emptyList()
)
