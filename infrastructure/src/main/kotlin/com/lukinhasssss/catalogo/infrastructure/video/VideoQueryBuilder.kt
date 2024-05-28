package com.lukinhasssss.catalogo.infrastructure.video

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Option

class VideoQueryBuilder(vararg opts: Option) {
    private val must: MutableList<Query> = mutableListOf()

    init {
        opts.forEach { it.invoke(this) }
    }

    fun must(aQuery: Query): VideoQueryBuilder {
        must.add(aQuery)
        return this
    }

    fun build(): Query = QueryBuilders.bool { b -> b.must(must) }

    fun interface Option : (VideoQueryBuilder) -> Unit

    companion object {
        private val NOOP: Option = Option { }

        fun onlyPublished(): Option = Option { b -> b.must(QueryBuilders.term { t -> t.field("published").value(true) }) }

        fun containingCategories(categories: Set<String>): Option {
            if (categories.isEmpty()) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.terms { t -> t.field("categories").terms { it.value(fieldValues(categories)) } }) }
        }

        fun containingCastMembers(members: Set<String>): Option {
            if (members.isEmpty()) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.terms { t -> t.field("cast_members").terms { it.value(fieldValues(members)) } }) }
        }

        fun containingGenres(genres: Set<String>): Option {
            if (genres.isEmpty()) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.terms { t -> t.field("genres").terms { it.value(fieldValues(genres)) } }) }
        }

        fun launchedAtEquals(launchedAt: Int?): Option {
            if (launchedAt == null) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.term { t -> t.field("launched_at").value(launchedAt.toLong()) }) }
        }

        fun ratingEquals(rating: String?): Option {
            if (rating.isNullOrBlank()) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.term { t -> t.field("rating").value(rating) }) }
        }

        fun titleOrDescriptionContaining(terms: String?): Option {
            if (terms.isNullOrBlank()) {
                return NOOP
            }

            return Option { b -> b.must(QueryBuilders.queryString { q -> q.fields("title", "description").query("*$terms*") }) }
        }

        private fun fieldValues(ids: Set<String>): List<FieldValue> = ids.map { FieldValue.of(it) }
    }
}
