package com.lukinhasssss.catalogo.application.video.delete

import com.lukinhasssss.catalogo.application.UnitUseCase
import com.lukinhasssss.catalogo.domain.video.VideoGateway

class DeleteVideoUseCase(
    private val videoGateway: VideoGateway
) : UnitUseCase<DeleteVideoUseCase.Input>() {

    override fun execute(input: Input) = with(input) {
        if (this.videoId.isNullOrBlank()) return
        videoGateway.deleteById(this.videoId)
    }

    data class Input(
        val videoId: String?
    )
}
