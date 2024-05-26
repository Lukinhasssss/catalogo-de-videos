package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreDocument
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreRepository
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.SearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.Criteria.where
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
@Profile("!development")
class GenreElasticsearchGateway(
    private val genreRepository: GenreRepository,
    private val searchOperations: SearchOperations
) : GenreGateway {

    companion object {
        private const val NAME_PROP = "name"
        private const val KEYWORD_SUFIX = ".keyword"
    }

    override fun save(genre: Genre): Genre {
        genreRepository.save(GenreDocument.from(genre))
        return genre
    }

    override fun findById(id: String): Genre? =
        genreRepository.findById(id).getOrNull()?.run { toGenre() }

    override fun findAll(aQuery: GenreSearchQuery): Pagination<Genre> = with(aQuery) {
        val sort = Sort.by(Sort.Direction.fromString(direction), buildSort(sort))

        val query = PageRequest.of(page, perPage, sort).let {
            if (terms.isEmpty() && categories.isEmpty()) {
                Query.findAll().setPageable<Query>(it)
            } else {
                CriteriaQuery(createCriteria(), it)
            }
        }

        val result = searchOperations.search(query, GenreDocument::class.java)

        val total = result.totalHits

        val castMembers = result.map { it.content }.map { it.toGenre() }.toList()

        Pagination(page, perPage, total, castMembers)
    }

    override fun findAllById(ids: Set<String>): List<Genre> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            genreRepository.findAllById(ids).map { it.toGenre() }
        }

    override fun deleteById(id: String) = genreRepository.deleteById(id)

    private fun buildSort(sort: String) = when (sort) {
        NAME_PROP -> sort.plus(KEYWORD_SUFIX)
        else -> sort
    }

    private fun GenreSearchQuery.createCriteria(): Criteria {
        var criteria: Criteria? = null

        if (terms.isNotEmpty()) {
            criteria = where("name").contains(terms)
        }

        if (categories.isNotEmpty()) {
            val categoriesWhere = where("categories").`in`(categories)
            criteria = criteria?.and(categoriesWhere) ?: categoriesWhere
        }

        return criteria!!
    }
}
