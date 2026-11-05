package ir.adicom.kamkam.ui.report

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.PeriodCalendar
import ir.adicom.kamkam.data.ReportPeriod
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.ui.home.AndroidHomeFormatter
import ir.adicom.kamkam.ui.home.HomeFormatter
import java.text.NumberFormat
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportFormatterModule {
    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideReportFormatter(
        @ApplicationContext context: Context,
        clock: Clock
    ): ReportFormatter {
        return AndroidReportFormatter(context, clock.zone)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun providePeriodCalendar(
        clock: Clock
    ): PeriodCalendar {
        return PeriodCalendar(clock.zone)
    }
}

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fmt(pattern: String, d: LocalDate): String =
        SimpleDateFormat(pattern, locale).apply { timeZone = TimeZone.getTimeZone(zone.id) }
            .format(Date(d.startMillis(zone)))

    @RequiresApi(Build.VERSION_CODES.O)
    override fun weekdayInitial(d: LocalDate) = fmt("EEEEE", d)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun dayNumber(d: LocalDate) = fmt("d", d)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun monthName(d: LocalDate) =
        fmt("MMMM", d)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun fullDay(d: LocalDate) = fmt("EEEE d MMMM", d)

    @RequiresApi(Build.VERSION_CODES.O)
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