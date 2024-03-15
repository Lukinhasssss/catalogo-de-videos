package com.lukinhasssss.catalogo.domain.castmember

import com.lukinhasssss.catalogo.domain.pagination.Pagination

interface CastMemberGateway {

    fun save(aCategory: CastMember): CastMember

    fun findById(anId: String): CastMember?

    fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember>

    fun deleteById(anID: String)
}
