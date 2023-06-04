package com.lukinhasssss.catalogo.domain.pagination

data class Pagination<T>(
    val meta: Metadata,
    val data: List<T>
) {

    constructor(
        currentPage: Int,
        perPage: Int,
        total: Long,
        items: List<T>
    ) : this(
        meta = Metadata(currentPage, perPage, total),
        data = items
    )

    fun <R> map(mapper: (T) -> R): Pagination<R> {
        val aNewList = data.stream().map(mapper).toList()

        return Pagination(meta, aNewList)
    }
}
