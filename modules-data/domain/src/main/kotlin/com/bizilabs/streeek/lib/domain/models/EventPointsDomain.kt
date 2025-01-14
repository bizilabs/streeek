package com.bizilabs.streeek.lib.domain.models

sealed class EventPointsDomain(
    open val event: String,
    open val description: String
) {

    data class Single(
        override val event: String,
        override val description: String,
        val points: Int
    ) : EventPointsDomain(event = event, description = description)

    data class Multiple(
        override val event: String,
        override val description: String,
        val points: List<Int>
    ) : EventPointsDomain(event = event, description = description)

}
