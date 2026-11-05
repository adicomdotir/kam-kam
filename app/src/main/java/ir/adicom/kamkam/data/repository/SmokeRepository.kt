package ir.adicom.kamkam.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import ir.adicom.kamkam.data.SuggestedBaseline
import ir.adicom.kamkam.data.dao.SmokeLogDao
import ir.adicom.kamkam.data.entity.SmokeLog
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.data.suggestBaseline
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SmokeRepository @Inject constructor(private val dao: SmokeLogDao) {
    fun observeBetween(from: Long, to: Long): Flow<List<SmokeLog>> = dao.observeBetween(from, to)
    fun observeLastTimestamp(): Flow<Long?> = dao.observeLastTimestamp()

    fun observeTimestampsBetween(from: Long, to: Long): Flow<List<Long>> = dao.observeTimestampsBetween(from, to)

    suspend fun log(timestamp: Long): Long = dao.insert(SmokeLog(timestamp = timestamp))

    suspend fun getById(id: Long): SmokeLog? = dao.getById(id)

    /** id == 0 یعنی ثبت جدید */
    suspend fun save(log: SmokeLog): Long =
        if (log.id == 0L) dao.insert(log) else { dao.update(log); log.id }

    suspend fun updateDetails(id: Long, triggerId: Long?, intensity: Int?) =
        dao.updateDetails(id, triggerId, intensity)

    suspend fun softDelete(id: Long, now: Long) = dao.softDelete(id, now)
    suspend fun restore(id: Long) = dao.restore(id)
    suspend fun purgeDeletedBefore(before: Long) = dao.purgeDeletedBefore(before)

    /** میانگین روزانه‌ی حداکثر ۷ روز اول ثبت (روز جاری هم حساب می‌شود). */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun suggestedBaseline(today: LocalDate, zone: ZoneId): SuggestedBaseline? {
        val first = dao.firstTimestamp() ?: return null
        val startDate = Instant.ofEpochMilli(first).atZone(zone).toLocalDate()
        val days = (ChronoUnit.DAYS.between(startDate, today).toInt() + 1).coerceIn(1, 7)
        val from = startDate.startMillis(zone)
        val to = startDate.plusDays(days.toLong()).startMillis(zone)
        return suggestBaseline(dao.countBetween(from, to), days)
    }
}
