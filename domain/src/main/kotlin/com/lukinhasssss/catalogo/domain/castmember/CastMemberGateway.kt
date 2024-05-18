package com.lukinhasssss.catalogo.domain.castmember

import com.lukinhasssss.catalogo.domain.pagination.Pagination

interface CastMemberGateway {

    fun save(aMember: CastMember): CastMember

    fun findById(anId: String): CastMember?

    fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember>

    fun findAllById(ids: List<String>): List<CastMember>

    fun deleteById(anId: String)
}
