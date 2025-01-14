package com.bizilabs.streeek.feature.points

import cafe.adriel.voyager.core.model.StateScreenModel
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.domain.models.points.EventPointsDomain
import com.bizilabs.streeek.lib.domain.repositories.PointsRepository
import kotlinx.coroutines.flow.update
import org.koin.dsl.module

val FeaturePoints =
    module {
        factory { PointsScreenModel(repository = get()) }
    }

data class PointsScreenState(
    val fetch: FetchListState<EventPointsDomain> = FetchListState.Loading,
)

class PointsScreenModel(
    private val repository: PointsRepository,
) : StateScreenModel<PointsScreenState>(PointsScreenState()) {
    init {
        getPoints()
    }

    private fun getPoints() {
        mutableState.update { it.copy(fetch = FetchListState.Success(repository.getPointsData())) }
    }
}
