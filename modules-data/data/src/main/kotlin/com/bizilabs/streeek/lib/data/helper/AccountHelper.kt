package com.bizilabs.streeek.lib.data.helper

import com.bizilabs.streeek.lib.data.mappers.toDomain
import com.bizilabs.streeek.lib.local.sources.account.AccountLocalSource
import kotlinx.coroutines.flow.firstOrNull

abstract class AccountHelper(
    private val source: AccountLocalSource,
) {
    suspend fun getAccount() = source.account.firstOrNull()?.toDomain()

    suspend fun getAccountId(): Long? = getAccount()?.id
}
