package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberDocument
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberRepository
import org.springframework.stereotype.Component

@Component
class CastMemberElasticsearchGateway(
    private val castMemberRepository: CastMemberRepository
) : CastMemberGateway {
    override fun save(aMember: CastMember): CastMember {
        castMemberRepository.save(CastMemberDocument.from(aMember))
        return aMember
    }

    override fun findById(anId: String): CastMember? {
        TODO("Not yet implemented")
    }

    override fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember> {
        TODO("Not yet implemented")
    }

    override fun deleteById(anId: String) {
        TODO("Not yet implemented")
    }
}
