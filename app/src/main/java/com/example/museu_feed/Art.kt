package com.example.museu_feed

data class Art(
    val title: String,
    val author: String,
    val qrCode: String,
    val imageUrl: String,
    val comments: List<String>
)