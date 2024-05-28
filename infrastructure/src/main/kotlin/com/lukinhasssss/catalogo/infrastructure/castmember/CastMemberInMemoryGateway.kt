package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("development")
class CastMemberInMemoryGateway(
    private val db: MutableMap<String, CastMember> = mutableMapOf()
) : CastMemberGateway {

    override fun save(aMember: CastMember): CastMember {
        db[aMember.id] = aMember
        return aMember
    }

    override fun findById(anId: String): CastMember? {
        return db[anId]
    }

    override fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember> = with(aQuery) {
        Pagination(
            currentPage = page,
            perPage = perPage,
            total = db.size.toLong(),
            items = db.values.toList()
        )
    }

    override fun findAllById(ids: Set<String>): List<CastMember> =
        db.filterKeys { it in ids }.values.toList()

    override fun deleteById(anId: String) {
        if (db.containsKey(anId)) db.remove(anId)
    }
}
