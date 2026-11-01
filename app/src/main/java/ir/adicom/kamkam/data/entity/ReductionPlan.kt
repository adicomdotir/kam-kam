package ir.adicom.kamkam.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "reduction_plans", indices = [Index("isActive")])
data class ReductionPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startEpochDay: Long,
    val baseline: Int,
    val stepSize: Int,
    val stepDays: Int,
    val finalTarget: Int = 0,
    val extraDays: Int = 0,
    val isActive: Boolean = true,
) {
    fun targetForEpochDay(epochDay: Long): Int {
        val elapsed = (epochDay - startEpochDay - extraDays).coerceAtLeast(0)
        val steps = elapsed / stepDays.coerceAtLeast(1)
        val reduced = steps * stepSize
        return (baseline - reduced).coerceAtLeast(finalTarget.toLong()).toInt()
    }
}