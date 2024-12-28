package com.bizilabs.streeek.lib.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val number: Long,
    val minPoints: Long,
    val maxPoints: Long,
    val createdAt: String
)
