package ir.adicom.kamkam.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ir.adicom.kamkam.data.entity.ReductionPlan
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReductionPlanDao {
    @Insert
    abstract suspend fun insert(plan: ReductionPlan): Long

    @Update
    abstract suspend fun update(plan: ReductionPlan)

    @Query("SELECT * FROM reduction_plans WHERE isActive = 1 LIMIT 1")
    abstract fun observeActive(): Flow<ReductionPlan?>

    @Query("UPDATE reduction_plans SET isActive = 0 WHERE isActive = 1")
    abstract suspend fun deactivateAll()

    /** همیشه فقط یک برنامه‌ی فعال وجود دارد. */
    @Transaction
    open suspend fun replaceActive(plan: ReductionPlan): Long {
        deactivateAll()
        return insert(plan.copy(isActive = true))
    }
}