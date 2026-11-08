package ir.adicom.kamkam.data.repository

import androidx.room.withTransaction
import ir.adicom.kamkam.data.database.AppDatabase
import ir.adicom.kamkam.data.entity.CravingLog
import ir.adicom.kamkam.data.entity.SmokeLog
import javax.inject.Inject

class CravingRepository @Inject constructor (private val db: AppDatabase) {

    /** هوسی که رد شد؛ timestamp لحظه‌ی شروع هوس است. */
    suspend fun recordResisted(startedAt: Long, triggerId: Long?, durationSeconds: Int) {
        db.cravingLogDao().insert(
            CravingLog(
                timestamp = startedAt,
                resisted = true,
                triggerId = triggerId,
                durationSeconds = durationSeconds
            )
        )
    }

    /** هوس به سیگار ختم شد: هم هوس و هم نخ در یک تراکنش ثبت می‌شوند تا شمارنده درست بماند. */
    suspend fun recordSmoked(startedAt: Long, smokedAt: Long, triggerId: Long?, durationSeconds: Int) {
        db.withTransaction {
            db.cravingLogDao().insert(
                CravingLog(timestamp = startedAt, resisted = false, triggerId = triggerId, durationSeconds = durationSeconds)
            )
            db.smokeLogDao().insert(SmokeLog(timestamp = smokedAt, triggerId = triggerId))
        }
    }
}
