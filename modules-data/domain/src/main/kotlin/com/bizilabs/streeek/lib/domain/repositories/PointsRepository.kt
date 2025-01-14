package com.bizilabs.streeek.lib.domain.repositories

import com.bizilabs.streeek.lib.domain.models.points.EventPointsDomain
import com.bizilabs.streeek.lib.domain.models.points.getAllPoints

class PointsRepository {
    fun getPointsData(): List<EventPointsDomain> = getAllPoints()
}
