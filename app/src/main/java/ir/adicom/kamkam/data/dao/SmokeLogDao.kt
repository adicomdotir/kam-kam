package ir.adicom.kamkam.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.adicom.kamkam.data.entity.SmokeLog
import kotlinx.coroutines.flow.Flow

data class TriggerCount(val triggerId: Long, val count: Int)

@Dao
interface SmokeLogDao {
    @Insert
    suspend fun insert(log: SmokeLog): Long

    @Update
    suspend fun update(log: SmokeLog)

    @Query("SELECT * FROM smoke_logs WHERE id = :id")
    suspend fun getById(id: Long): SmokeLog?

    @Query("UPDATE smoke_logs SET deletedAt = :now WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("UPDATE smoke_logs SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM smoke_logs WHERE deletedAt IS NOT NULL AND deletedAt < :before")
    suspend fun purgeDeletedBefore(before: Long)

    @Query(
        "SELECT * FROM smoke_logs " +
                "WHERE deletedAt IS NULL AND timestamp >= :from AND timestamp < :to " +
                "ORDER BY timestamp DESC"
    )
    fun observeBetween(from: Long, to: Long): Flow<List<SmokeLog>>

    @Query(
        "SELECT COUNT(*) FROM smoke_logs " +
                "WHERE deletedAt IS NULL AND timestamp >= :from AND timestamp < :to"
    )
    fun observeCountBetween(from: Long, to: Long): Flow<Int>

    @Query("SELECT MAX(timestamp) FROM smoke_logs WHERE deletedAt IS NULL")
    fun observeLastTimestamp(): Flow<Long?>

    /** برای گزارش‌ها: گروه‌بندی روز/هفته/ماه در Kotlin و با منطقه‌ی زمانی کاربر انجام می‌شود. */
    @Query(
        "SELECT timestamp FROM smoke_logs " +
                "WHERE deletedAt IS NULL AND timestamp >= :from AND timestamp < :to " +
                "ORDER BY timestamp"
    )
    fun observeTimestampsBetween(from: Long, to: Long): Flow<List<Long>>

    /** برای U7 (بعد از MVP) */
    @Query(
        "SELECT triggerId, COUNT(*) AS count FROM smoke_logs " +
                "WHERE deletedAt IS NULL AND triggerId IS NOT NULL " +
                "AND timestamp >= :from AND timestamp < :to " +
                "GROUP BY triggerId ORDER BY count DESC"
    )
    fun observeTriggerCounts(from: Long, to: Long): Flow<List<TriggerCount>>
}