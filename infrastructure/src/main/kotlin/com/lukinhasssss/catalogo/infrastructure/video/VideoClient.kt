package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.infrastructure.video.models.VideoDTO

fun interface VideoClient {
    fun videoOfId(videoId: String?): VideoDTO?
}
