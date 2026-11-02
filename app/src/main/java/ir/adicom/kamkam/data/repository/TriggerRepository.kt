package ir.adicom.kamkam.data.repository

import ir.adicom.kamkam.data.dao.TriggerDao
import ir.adicom.kamkam.data.entity.Trigger
import kotlinx.coroutines.flow.Flow

class TriggerRepository(private val dao: TriggerDao) {
    fun observeAll(): Flow<List<Trigger>> = dao.observeAll()
}