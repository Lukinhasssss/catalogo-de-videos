package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.stereotype.Component

@Component
class CastMemberElasticsearchGateway : CastMemberGateway {
    override fun save(aMember: CastMember): CastMember {
        TODO("Not yet implemented")
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