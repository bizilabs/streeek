package com.bizilabs.streeek.lib.domain.models.points

sealed class EventPointsDomain(
    open val title: String,
    open val subtitle: String,
    open val description: String,
) {
    data class Single(
        override val title: String,
        override val subtitle: String = "",
        override val description: String = "",
        val points: Int,
    ) : EventPointsDomain(
            title = title,
            subtitle = subtitle,
            description = description,
        )

    data class Multiple(
        override val title: String,
        override val subtitle: String = "",
        override val description: String = "",
        val actions: List<MultiplePoint>,
    ) : EventPointsDomain(
            title = title,
            subtitle = subtitle,
            description = description,
        ) {
        data class MultiplePoint(
            val action: String,
            val points: Int,
            val description: String,
        )
    }
}
