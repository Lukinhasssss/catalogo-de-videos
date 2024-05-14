package com.lukinhasssss.catalogo.application.castmember.delete

import com.lukinhasssss.catalogo.application.UnitUseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway

class DeleteCastMemberUseCase(
    private val castMemberGateway: CastMemberGateway
) : UnitUseCase<String?>() {

    override fun execute(input: String?) = with(input) {
        if (input.isNullOrBlank()) return

        castMemberGateway.deleteById(this!!)
    }
}
