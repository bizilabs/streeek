package com.bizilabs.streeek.feature.tabs.screens.achievements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.LevelDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import kotlin.enums.EnumEntries

internal val AchievementsModule = module {
    factory<AchievementsScreenModel> {
        AchievementsScreenModel(
            accountRepository = get()
        )
    }
}

enum class AchievementTab {
    BADGES, LEVELS;

    val label: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val icon: Pair<ImageVector, ImageVector>
        get() = when (this) {
            BADGES -> Pair(Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
            LEVELS -> Pair(Icons.Filled.Stairs, Icons.Outlined.Stairs)
        }

}

data class AchievementScreenState(
    val account: AccountDomain? = null,
    val tab: AchievementTab = AchievementTab.BADGES,
    val tabs: EnumEntries<AchievementTab> = AchievementTab.entries
) {
    val points: Long
        get() = account?.points ?: 0

    val level: LevelDomain?
        get() = account?.level
}

class AchievementsScreenModel(
    private val accountRepository: AccountRepository
) : StateScreenModel<AchievementScreenState>(AchievementScreenState()) {

    init {
        initiateAccountSync()
        observeAccount()
    }

    private fun initiateAccountSync() {
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

    fun onClickTab(tab: AchievementTab) {
        mutableState.update { it.copy(tab = tab) }
    }

}