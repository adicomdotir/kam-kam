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
    /** روزهای تمدیدشده؛ تمدید مرحله یعنی افزودن به این عدد و جابه‌جا شدن همه‌ی مرحله‌های بعدی */
    val extraDays: Int = 0,
    /** آخرین روزی که کاربر تمدید را قبول کرد؛ روزهای قبل از آن در پیشنهاد تمدید حساب نمی‌شوند. */
    val lastExtendedOnEpochDay: Long? = null,
    val isActive: Boolean = true,
) {
    fun targetForEpochDay(epochDay: Long): Int {
        val elapsed = (epochDay - startEpochDay - extraDays).coerceAtLeast(0)
        val steps = elapsed / stepDays.coerceAtLeast(1)
        val reduced = steps * stepSize
        return (baseline - reduced).coerceAtLeast(finalTarget.toLong()).toInt()
    }

    /** شماره‌ی مرحله‌ی جاری، از ۱ شروع می‌شود. */
    fun stageForEpochDay(epochDay: Long): Int {
        val elapsed = (epochDay - startEpochDay - extraDays).coerceAtLeast(0)
        return (elapsed / stepDays.coerceAtLeast(1)).toInt() + 1
    }
}