package ir.adicom.kamkam.data.repository

import ir.adicom.kamkam.data.dao.TriggerDao
import ir.adicom.kamkam.data.entity.Trigger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TriggerRepository @Inject constructor(private val dao: TriggerDao) {
    fun observeAll(): Flow<List<Trigger>> = dao.observeAll()
}

