package ir.adicom.kamkam.ui.report

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.icu.util.ULocale
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.startMillis
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

interface ReportFormatter {
    fun weekdayInitial(d: LocalDate): String
    fun dayNumber(d: LocalDate): String
    fun monthName(d: LocalDate): String
    /** «یکشنبه ۱۰ آبان» */
    fun fullDay(d: LocalDate): String
    /** عنوان بازه، مثل «۴ تا ۱۰ آبان» */
    fun range(period: ReportPeriod, first: LocalDate, lastInclusive: LocalDate): String
    fun average(value: Double): String
    fun todaySuffix(): String
}

class AndroidReportFormatter(
    private val context: Context,
    private val zone: ZoneId,
) : ReportFormatter {

    private val locale = ULocale("fa_IR@calendar=persian")

    private fun fmt(pattern: String, d: LocalDate): String =
        SimpleDateFormat(pattern, locale).apply { timeZone = TimeZone.getTimeZone(zone.id) }
            .format(Date(d.startMillis(zone)))

    override fun weekdayInitial(d: LocalDate) = fmt("EEEEE", d)
    override fun dayNumber(d: LocalDate) = fmt("d", d)
    override fun monthName(d: LocalDate) = fmt("MMMM", d)
    override fun fullDay(d: LocalDate) = fmt("EEEE d MMMM", d)

    override fun range(period: ReportPeriod, first: LocalDate, lastInclusive: LocalDate): String {
        if (period == ReportPeriod.MONTHLY) {
            return context.getString(R.string.report_range_cross, monthName(first), monthName(lastInclusive))
        }
        val sameMonth = monthName(first) == monthName(lastInclusive)
        return if (sameMonth) {
            context.getString(R.string.report_range_same_month, dayNumber(first), dayNumber(lastInclusive), monthName(first))
        } else {
            context.getString(
                R.string.report_range_cross,
                "${dayNumber(first)} ${monthName(first)}",
                "${dayNumber(lastInclusive)} ${monthName(lastInclusive)}",
            )
        }
    }

    override fun todaySuffix(): String = context.getString(R.string.day_today)

    override fun average(value: Double): String =
        NumberFormat.getInstance(Locale("fa")).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }.format(value)
}