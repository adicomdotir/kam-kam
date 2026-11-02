package ir.adicom.kamkam.data.repository

import ir.adicom.kamkam.data.dao.SmokeLogDao
import ir.adicom.kamkam.data.entity.SmokeLog
import kotlinx.coroutines.flow.Flow

class SmokeRepository(private val dao: SmokeLogDao) {
    fun observeBetween(from: Long, to: Long): Flow<List<SmokeLog>> = dao.observeBetween(from, to)
    fun observeLastTimestamp(): Flow<Long?> = dao.observeLastTimestamp()

    suspend fun log(timestamp: Long): Long = dao.insert(SmokeLog(timestamp = timestamp))
    suspend fun updateDetails(id: Long, triggerId: Long?, intensity: Int?) =
        dao.updateDetails(id, triggerId, intensity)

    suspend fun softDelete(id: Long, now: Long) = dao.softDelete(id, now)
    suspend fun restore(id: Long) = dao.restore(id)
    suspend fun purgeDeletedBefore(before: Long) = dao.purgeDeletedBefore(before)
}

