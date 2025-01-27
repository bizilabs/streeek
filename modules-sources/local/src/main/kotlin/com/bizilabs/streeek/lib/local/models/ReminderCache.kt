package com.bizilabs.streeek.lib.local.models

import kotlinx.serialization.Serializable

@Serializable
data class ReminderCache(
    val label: String,
    val repeat: List<Int>,
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
)
