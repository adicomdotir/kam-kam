package ir.adicom.kamkam.data.repository

import ir.adicom.kamkam.data.dao.ReductionPlanDao
import ir.adicom.kamkam.data.entity.ReductionPlan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlanRepository @Inject constructor(private val dao: ReductionPlanDao) {
    fun observeActive(): Flow<ReductionPlan?> = dao.observeActive()
}