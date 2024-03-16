package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CastMemberInMemoryGateway : CastMemberGateway {

    private val db: MutableMap<String, CastMember> = ConcurrentHashMap()

    override fun save(aMember: CastMember): CastMember {
        db[aMember.id] = aMember
        return aMember
    }

    override fun deleteById(anId: String) {
        db.remove(anId)
    }

    override fun findById(anId: String): CastMember? = db[anId]

    override fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember> = with(aQuery) {
        Pagination(page, perPage, db.size.toLong(), db.values.toList())
    }
}
