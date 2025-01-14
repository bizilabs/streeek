package com.bizilabs.streeek.feature.points

import cafe.adriel.voyager.core.model.StateScreenModel
import org.koin.dsl.module

val FeaturePoints =
    module {
        factory { PointsScreenModel() }
    }

data class PointsScreenState(
    val list: List<String> = emptyList()
)

class PointsScreenModel(

) : StateScreenModel<PointsScreenState>(PointsScreenState()) {

}