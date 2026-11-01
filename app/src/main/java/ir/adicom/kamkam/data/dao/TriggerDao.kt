package ir.adicom.kamkam.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.adicom.kamkam.data.entity.Trigger
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerDao {
    @Insert
    suspend fun insert(trigger: Trigger): Long

    @Update
    suspend fun update(trigger: Trigger)

    @Query("UPDATE triggers SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("SELECT * FROM triggers WHERE isArchived = 0 ORDER BY id")
    fun observeActive(): Flow<List<Trigger>>

    /** گزارش‌ها باید اسم محرک‌های بایگانی‌شده را هم نشان بدهند. */
    @Query("SELECT * FROM triggers")
    fun observeAll(): Flow<List<Trigger>>
}