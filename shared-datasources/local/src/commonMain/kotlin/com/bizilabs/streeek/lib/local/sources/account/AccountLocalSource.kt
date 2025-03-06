package com.bizilabs.streeek.lib.local.sources.account

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bizilabs.streeek.lib.local.models.AccountCache
import com.bizilabs.streeek.lib.local.models.fromJsonToAccountCache
import com.bizilabs.streeek.lib.local.sources.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

interface AccountLocalSource {
    val account: Flow<AccountCache?>
    val isSyncingAccount: Flow<Boolean>

    suspend fun updateAccount(account: AccountCache)

    suspend fun updateIsSyncingAccount(value: Boolean)

    suspend fun logout()
}

class AccountLocalSourceImpl(
    private val preferenceSource: PreferenceSource,
) : AccountLocalSource {
    object Keys {
        val account = stringPreferencesKey("account")
        val isSyncingAccount = booleanPreferencesKey("account.syncing")
    }

    override val account: Flow<AccountCache?>
        get() =
            preferenceSource.getNullable(key = Keys.account)
                .mapLatest { it?.fromJsonToAccountCache() }

    override val isSyncingAccount: Flow<Boolean>
        get() = preferenceSource.get(key = Keys.isSyncingAccount, default = false)

    override suspend fun updateAccount(account: AccountCache) {
        preferenceSource.update(key = Keys.account, value = account.asJson())
    }

    override suspend fun updateIsSyncingAccount(value: Boolean) {
        preferenceSource.update(key = Keys.isSyncingAccount, value = value)
    }

    override suspend fun logout() {
        preferenceSource.clear()
    }
}
