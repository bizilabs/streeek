package com.bizilabs.streeek.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.design.atoms.SafiTypography
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.domain.repositories.VersionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import java.util.EnumSet
import kotlin.enums.EnumEntries

val profileModule =
    module {
        factory { ProfileScreenModel(accountRepository = get(), versionRepository = get(), preferenceRepository = get()) }
    }

data class ProfileScreenState(
    val account: AccountDomain? = null,
    val shouldConfirmLogout: Boolean = false,
    val shouldNavigateToLanding: Boolean = false,
    val versionCode: Int = 1,
    val versionName: String = "",
    val typographies: EnumEntries<SafiTypography> = SafiTypography.entries,
    val typography: SafiTypography = SafiTypography.MONO,
    val isSelectingTypography: Boolean = false
)

class ProfileScreenModel(
    private val accountRepository: AccountRepository,
    private val versionRepository: VersionRepository,
    private val preferenceRepository: PreferenceRepository
) : StateScreenModel<ProfileScreenState>(ProfileScreenState()) {
    init {
        getAccount()
        observeAccount()
        getVersion()
        observeTypography()
    }

    private fun getVersion() {
        mutableState.update {
            it.copy(
                versionCode = versionRepository.code,
                versionName = versionRepository.name,
            )
        }
    }

    private fun getAccount() {
        screenModelScope.launch {
            accountRepository.syncAccount()
        }
    }

    private fun observeAccount() {
        screenModelScope.launch {
            accountRepository.account.collectLatest { account ->
                mutableState.update { it.copy(account = account) }
            }
        }
    }

    private fun observeTypography(){
        screenModelScope.launch {
            preferenceRepository.typography.collectLatest { typography ->
                mutableState.update { it.copy(typography = SafiTypography.valueOf(typography)) }
            }
        }
    }

    fun onClickLogout() {
        mutableState.update { it.copy(shouldConfirmLogout = true) }
    }

    fun onClickConfirmLogout(confirm: Boolean) {
        when (confirm) {
            true -> onClickConfirmLogoutYes()
            false -> onClickConfirmLogoutNo()
        }
    }

    fun onClickConfirmLogoutYes() {
        mutableState.update { it.copy(shouldConfirmLogout = false) }
        screenModelScope.launch {
            accountRepository.logout()
            mutableState.update { it.copy(shouldNavigateToLanding = true) }
        }
    }

    fun onClickConfirmLogoutNo() {
        mutableState.update { it.copy(shouldConfirmLogout = false) }
    }

    fun onToggleSelectTypography(show: Boolean){
        mutableState.update { it.copy(isSelectingTypography = show) }
    }

    fun onClickTypography(typography: SafiTypography){
        screenModelScope.launch {
            preferenceRepository.updateTypography(typography.name)
            mutableState.update { it.copy(isSelectingTypography = false) }
        }
    }
}
