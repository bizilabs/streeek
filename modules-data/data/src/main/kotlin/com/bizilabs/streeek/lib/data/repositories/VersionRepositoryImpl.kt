package com.bizilabs.streeek.lib.data.repositories

import com.bizilabs.streeek.lib.domain.repositories.VersionRepository

class VersionRepositoryImpl(
    val versionCode: Int,
    val versionName: String,
) : VersionRepository {
    override val code: Int
        get() = versionCode
    override val name: String
        get() = versionName
}
