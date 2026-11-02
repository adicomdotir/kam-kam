package ir.adicom.kamkam.data.repository

import ir.adicom.kamkam.data.dao.ReductionPlanDao
import ir.adicom.kamkam.data.entity.ReductionPlan
import kotlinx.coroutines.flow.Flow

class PlanRepository(private val dao: ReductionPlanDao) {
    fun observeActive(): Flow<ReductionPlan?> = dao.observeActive()
}