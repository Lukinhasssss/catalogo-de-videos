package com.lukinhasssss.catalogo.domain.video

enum class Rating(val description: String) {
    ER(description = "ER"),
    L(description = "L"),
    AGE_10(description = "10"),
    AGE_12(description = "12"),
    AGE_14(description = "14"),
    AGE_16(description = "16"),
    AGE_18(description = "18");

    override fun toString(): String {
        return description
    }

    companion object {
        fun of(label: String): Rating = entries.firstOrNull {
            it.name.equals(label, ignoreCase = true) || it.description.equals(label, ignoreCase = true)
        } ?: throw IllegalArgumentException("Invalid rating")
    }
}
