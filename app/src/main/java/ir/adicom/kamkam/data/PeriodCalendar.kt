package ir.adicom.kamkam.data

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

enum class ReportPeriod(val bucketCount: Int) {
    DAILY(7),      // ۷ روز
    WEEKLY(8),     // ۸ هفته (از شنبه)
    MONTHLY(6),    // ۶ ماه شمسی
}

/** بازه‌ی [start, endExclusive) به‌صورت تاریخ محلی کاربر */
data class Bucket(val start: LocalDate, val endExclusive: LocalDate) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun days(): Sequence<LocalDate> =
        generateSequence(start) { it.plusDays(1) }.takeWhile { it.isBefore(endExclusive) }

    @RequiresApi(Build.VERSION_CODES.O)
    operator fun contains(date: LocalDate): Boolean = !date.isBefore(start) && date.isBefore(endExclusive)
}

data class ReportWindow(val buckets: List<Bucket>) {
    val start: LocalDate get() = buckets.first().start
    val endExclusive: LocalDate get() = buckets.last().endExclusive
}

/**
 * ساخت بازه‌های گزارش. offset = ۰ یعنی بازه‌ی جاری، ۱ یعنی یک بازه‌ی قبل‌تر و ...
 * ماه‌های شمسی با ICU خود اندروید (API 24 به بالا) حساب می‌شوند.
 */
class PeriodCalendar(private val zone: ZoneId) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun window(period: ReportPeriod, today: LocalDate, offset: Int): ReportWindow = when (period) {
        ReportPeriod.DAILY -> {
            val last = today.minusDays(7L * offset)
            ReportWindow((6 downTo 0).map { back ->
                val d = last.minusDays(back.toLong())
                Bucket(d, d.plusDays(1))
            })
        }
        ReportPeriod.WEEKLY -> {
            val thisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))
            val lastWeek = thisWeek.minusWeeks(8L * offset)
            ReportWindow((7 downTo 0).map { back ->
                val s = lastWeek.minusWeeks(back.toLong())
                Bucket(s, s.plusDays(7))
            })
        }
        ReportPeriod.MONTHLY -> {
            ReportWindow((5 downTo 0).map { back ->
                val k = -(6 * offset) - back
                Bucket(monthStart(today, k), monthStart(today, k + 1))
            })
        }
    }

    /** اولین روز ماه شمسیِ [date] به‌علاوه‌ی shiftMonths ماه */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthStart(date: LocalDate, shiftMonths: Int): LocalDate {
        val cal = Calendar.getInstance(
            TimeZone.getTimeZone(zone.id),
            ULocale("fa_IR@calendar=persian")
        )
        cal.timeInMillis = date.startMillis(zone)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, shiftMonths)
        return Instant.ofEpochMilli(cal.timeInMillis).atZone(zone).toLocalDate()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun countPerDay(timestamps: List<Long>, zone: ZoneId): Map<LocalDate, Int> =
    timestamps.groupingBy { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }.eachCount()