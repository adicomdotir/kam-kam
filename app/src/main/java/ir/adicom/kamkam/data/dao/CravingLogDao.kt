package ir.adicom.kamkam.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.adicom.kamkam.data.entity.CravingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CravingLogDao {
    @Insert
    suspend fun insert(log: CravingLog): Long

    @Update
    suspend fun update(log: CravingLog)

    @Query("UPDATE craving_logs SET deletedAt = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE craving_logs SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM craving_logs WHERE deletedAt IS NOT NULL AND deletedAt < :before")
    suspend fun purgeDeletedBefore(before: Long)

    @Query(
        "SELECT * FROM craving_logs " +
                "WHERE deletedAt IS NULL AND timestamp >= :from AND timestamp < :to " +
                "ORDER BY timestamp DESC"
    )
    fun observeBetween(from: Long, to: Long): Flow<List<CravingLog>>

    @Query(
        "SELECT COUNT(*) FROM craving_logs " +
                "WHERE deletedAt IS NULL AND resisted = 1 AND timestamp >= :from AND timestamp < :to"
    )
    fun observeResistedCountBetween(from: Long, to: Long): Flow<Int>
}