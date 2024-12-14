package com.bizilabs.streeek.lib.local.sources.account

import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.local.models.fromJsonToAccountCache
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

interface AccountLocalSource {
    val account: Flow<AccountCache?>
    suspend fun updateAccount(account: AccountCache)
}

class AccountLocalSourceImpl(
    private val preferenceSource: PreferenceSource
) : AccountLocalSource {

    object Keys {
        val account = stringPreferencesKey("account")
    }

    override val account: Flow<AccountCache?>
        get() = preferenceSource.getNullable(key = Keys.account)
            .mapLatest { it?.fromJsonToAccountCache() }

    override suspend fun updateAccount(account: AccountCache) {
        preferenceSource.update(key = Keys.account, value = account.asJson())
    }

}