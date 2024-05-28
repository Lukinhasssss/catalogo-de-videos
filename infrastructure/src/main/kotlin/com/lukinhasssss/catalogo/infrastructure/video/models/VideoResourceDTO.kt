package com.lukinhasssss.catalogo.infrastructure.video.models

data class VideoResourceDTO(
    val id: String,
    val name: String,
    val checksum: String,
    val location: String,
    val encodedLocation: String,
    val status: String
)
