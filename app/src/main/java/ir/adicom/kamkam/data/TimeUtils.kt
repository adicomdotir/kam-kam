package ir.adicom.kamkam.data

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

/** شروع روز (۰۰:۰۰) به epoch millis در منطقه‌ی زمانی کاربر. بازه‌ی روز: [start, nextDayStart) */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.startMillis(zone: ZoneId): Long =
    atStartOfDay(zone).toInstant().toEpochMilli()

/** تاریخ امروز؛ با گذشتن از نیمه‌شب خودکار مقدار جدید می‌دهد. */
@RequiresApi(Build.VERSION_CODES.O)
fun todayFlow(clock: Clock): Flow<LocalDate> = flow {
    while (true) {
        val now = ZonedDateTime.now(clock)
        emit(now.toLocalDate())
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(clock.zone)
        delay(Duration.between(now, nextMidnight).toMillis() + 1_000)
    }
}.distinctUntilChanged()

/** برای به‌روز شدن «زمان از آخرین نخ» هر دقیقه. */
fun minuteTicker(): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(60_000)
    }
}