package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.DayOfWeek

data class ReminderDomain(
    val label: String,
    val repeat: List<DayOfWeek>,
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
)
