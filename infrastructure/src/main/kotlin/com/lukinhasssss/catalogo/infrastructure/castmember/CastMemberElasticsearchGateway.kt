package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberDocument
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberRepository
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.SearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria.where
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
@Profile("!development")
class CastMemberElasticsearchGateway(
    private val castMemberRepository: CastMemberRepository,
    private val searchOperations: SearchOperations
) : CastMemberGateway {

    companion object {
        private const val NAME_PROP = "name"
        private const val KEYWORD = ".keyword"
    }

    override fun save(aMember: CastMember): CastMember {
        castMemberRepository.save(CastMemberDocument.from(aMember))
        return aMember
    }

    override fun findById(anId: String): CastMember? =
        castMemberRepository.findById(anId).getOrNull()?.run { toCastMember() }

    override fun findAll(aQuery: CastMemberSearchQuery): Pagination<CastMember> = with(aQuery) {
        val sort = Sort.by(Sort.Direction.fromString(direction), buildSort(sort))

        val query = PageRequest.of(page, perPage, sort).let {
            if (terms.isNotBlank()) {
                CriteriaQuery(where("name").contains(terms), it)
            } else {
                Query.findAll().setPageable<Query>(it)
            }
        }

        val result = searchOperations.search(query, CastMemberDocument::class.java)

        val total = result.totalHits

        val castMembers = result.map { it.content }.map { it.toCastMember() }.toList()

        Pagination(page, perPage, total, castMembers)
    }

    override fun findAllById(ids: Set<String>): List<CastMember> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            castMemberRepository.findAllById(ids).map { it.toCastMember() }
        }

    override fun deleteById(anId: String) = castMemberRepository.deleteById(anId)

    private fun buildSort(sort: String) = when (sort) {
        NAME_PROP -> sort.plus(KEYWORD)
        else -> sort
    }
}
