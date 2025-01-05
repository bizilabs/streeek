package com.bizilabs.streeek.feature.tabs.screens.achievements

import android.content.Context
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
import com.bizilabs.streeek.lib.domain.repositories.LevelRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateAccountSyncWork
import com.bizilabs.streeek.lib.domain.workers.startPeriodicAccountSyncWork
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber
import kotlin.enums.EnumEntries

internal val AchievementsModule = module {
    factory<AchievementsScreenModel> {
        AchievementsScreenModel(context = get(), accountRepository = get(), levelRepository = get())
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
    val tab: AchievementTab = AchievementTab.LEVELS,
    val tabs: EnumEntries<AchievementTab> = AchievementTab.entries,
    val levels: List<LevelDomain> = emptyList()
) {
    val points: Long
        get() = account?.points ?: 0

    val level: LevelDomain?
        get() = account?.level
}

class AchievementsScreenModel(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val levelRepository: LevelRepository
) : StateScreenModel<AchievementScreenState>(AchievementScreenState()) {

    init {
        initiateAccountSync()
        observeAccount()
        observeLevels()
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

    private fun observeLevels() {
        screenModelScope.launch {
            levelRepository.levels.collectLatest { levels ->
                mutableState.update { it.copy(levels = levels) }
            }
        }
    }

    fun onClickTab(tab: AchievementTab) {
        mutableState.update { it.copy(tab = tab) }
    }

    fun onClickRefreshProfile() {
        context.startImmediateAccountSyncWork()
    }

}