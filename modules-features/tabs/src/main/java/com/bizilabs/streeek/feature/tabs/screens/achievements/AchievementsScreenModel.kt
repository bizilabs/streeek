package com.bizilabs.streeek.feature.tabs.screens.achievements

import android.app.Activity
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.reviews.ReviewManagerHelper
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.models.LevelDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.LevelRepository
import com.bizilabs.streeek.lib.domain.workers.startImmediateAccountSyncWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.dsl.module
import timber.log.Timber
import kotlin.enums.EnumEntries

internal val AchievementsModule =
    module {
        factory<AchievementsScreenModel> {
            AchievementsScreenModel(
                context = get(),
                accountRepository = get(),
                levelRepository = get(),
                reviewManagerHelper = get(),
            )
        }
    }

enum class AchievementTab {
    BADGES,
    LEVELS,
    ;

    val label: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val icon: Pair<ImageVector, ImageVector>
        get() =
            when (this) {
                BADGES -> Pair(Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
                LEVELS -> Pair(Icons.Filled.Stairs, Icons.Outlined.Stairs)
            }
}

data class AchievementScreenState(
    val account: AccountDomain? = null,
    val tab: AchievementTab = AchievementTab.LEVELS,
    val tabs: EnumEntries<AchievementTab> = AchievementTab.entries,
    val levels: List<LevelDomain> = emptyList(),
    val isSyncingAccount: Boolean = false,
    val reviewState: ReviewState = ReviewState.Idle,
) {
    val points: Long
        get() = account?.points ?: 0

    val level: LevelDomain?
        get() = account?.level
}

class AchievementsScreenModel(
    private val context: Context,
    private val accountRepository: AccountRepository,
    private val levelRepository: LevelRepository,
    private val reviewManagerHelper: ReviewManagerHelper,
) : StateScreenModel<AchievementScreenState>(AchievementScreenState()) {
    init {
        initiateAccountSync()
        observeAccountSync()
        observeAccount()
        observeLevels()
    }

    private fun initiateAccountSync() {
        screenModelScope.launch {
            accountRepository.syncAccount()
        }
    }

    private fun observeAccountSync() {
        screenModelScope.launch {
            accountRepository.isSyncingAccount.collectLatest { value ->
                mutableState.update { it.copy(isSyncingAccount = value) }
            }
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
                val currentLevel = state.value.level
                val lastVisibleLevel = (currentLevel?.number ?: 0) + 2
                mutableState.update {
                    it.copy(
                        levels =
                            levels.filterNot { it.number > lastVisibleLevel }
                                .sortedByDescending { it.number },
                    )
                }
            }
        }
    }

    fun onClickTab(tab: AchievementTab) {
        mutableState.update { it.copy(tab = tab) }
    }

    fun onClickRefreshProfile() {
        context.startImmediateAccountSyncWork()
    }

    fun requestReview(activity: Activity) {
        screenModelScope.launch {
            mutableState.update { it.copy(reviewState = ReviewState.Loading) }
            try {
                val result =
                    withContext(Dispatchers.IO) { reviewManagerHelper.triggerInAppReview(activity) }
                if (result.isSuccess) {
                    Timber.d("Successfully Launched In-App Review")
                    mutableState.update { it.copy(reviewState = ReviewState.Success) }
                } else {
                    val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                    Timber.d("Error Launching In-App Review: $errorMessage")
                    mutableState.update { it.copy(reviewState = ReviewState.Error(errorMessage)) }
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Unexpected error"
                Timber.d("Exception in In-App Review: $errorMessage")
                mutableState.update { it.copy(reviewState = ReviewState.Error(errorMessage)) }
            }
        }
    }
}

sealed class ReviewState {
    data object Idle : ReviewState()

    data object Loading : ReviewState()

    data object Success : ReviewState()

    data class Error(val message: String) : ReviewState()
}
