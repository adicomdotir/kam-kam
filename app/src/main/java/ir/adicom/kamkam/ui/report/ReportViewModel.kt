package ir.adicom.kamkam.ui.report

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.adicom.kamkam.data.PeriodCalendar
import ir.adicom.kamkam.data.ReportPeriod
import ir.adicom.kamkam.data.ReportWindow
import ir.adicom.kamkam.data.countPerDay
import ir.adicom.kamkam.data.entity.ReductionPlan
import ir.adicom.kamkam.data.hourCounts
import ir.adicom.kamkam.data.repository.PlanRepository
import ir.adicom.kamkam.data.repository.SmokeRepository
import ir.adicom.kamkam.data.startMillis
import ir.adicom.kamkam.data.todayFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @RequiresApi(Build.VERSION_CODES.O)
@Inject constructor(
    private val smoke: SmokeRepository,
    private val plans: PlanRepository,
    private val formatter: ReportFormatter,
    private val calendar: PeriodCalendar,
    private val clock: Clock,
) : ViewModel() {

    /** نوع بازه و فاصله‌ی آن تا بازه‌ی جاری؛ با هم عوض می‌شوند تا حالت میانی ساخته نشود. */
    private data class Selection(val period: ReportPeriod = ReportPeriod.DAILY, val offset: Int = 0)

    private val selection = MutableStateFlow(Selection())

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ReportUiState?> =
        combine(selection, todayFlow(clock)) { s, today -> s to today }
            .flatMapLatest { (sel, today) ->
                val window = calendar.window(sel.period, today, sel.offset)
                val from = window.start.startMillis(clock.zone)
                val to = window.endExclusive.startMillis(clock.zone)
                combine(smoke.observeTimestampsBetween(from, to), plans.observeActive()) { ts, plan ->
                    build(sel, today, window, countPerDay(ts, clock.zone), plan, buildHourlyUi(hourCounts(ts, clock.zone)))
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun goalFor(date: LocalDate, plan: ReductionPlan?): Int? {
        val day = date.toEpochDay()
        return plan?.takeIf { day >= it.startEpochDay }?.targetForEpochDay(day)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun build(
        sel: Selection,
        today: LocalDate,
        window: ReportWindow,
        perDay: Map<LocalDate, Int>,
        plan: ReductionPlan?,
        hourly: HourlyUi,
    ): ReportUiState {
        val bars = window.buckets.map { b ->
            val value = b.days().sumOf { perDay[it] ?: 0 }
            val isCurrent = today in b
            when (sel.period) {
                ReportPeriod.DAILY -> BarUi(
                    label = formatter.weekdayInitial(b.start),
                    sublabel = formatter.dayNumber(b.start),
                    value = value,
                    goal = goalFor(b.start, plan),
                    isCurrent = isCurrent,
                )
                ReportPeriod.WEEKLY -> BarUi(
                    label = formatter.dayNumber(b.start),
                    sublabel = formatter.monthName(b.start),
                    value = value, goal = null, isCurrent = isCurrent,
                )
                ReportPeriod.MONTHLY -> BarUi(
                    label = formatter.monthName(b.start),
                    sublabel = "",
                    value = value, goal = null, isCurrent = isCurrent,
                )
            }
        }

        // فقط روزهایی که گذشته‌اند یا امروزند در میانگین و «روز در هدف» حساب می‌شوند.
        val lastCounted = minOf(window.endExclusive.minusDays(1), today)
        val elapsedDays = window.buckets.flatMap { it.days().toList() }.filter { !it.isAfter(lastCounted) }
        val total = bars.sumOf { it.value }
        val average = total.toDouble() / elapsedDays.size.coerceAtLeast(1)

        val goalDays = elapsedDays.mapNotNull { d -> goalFor(d, plan)?.let { g -> (perDay[d] ?: 0) <= g } }
        val inGoal = if (goalDays.isEmpty()) null else InGoalUi(goalDays.count { it }, goalDays.size)

        val lastBucket = window.buckets.last()
        val rangeEnd = if (sel.period == ReportPeriod.MONTHLY) lastBucket.start else window.endExclusive.minusDays(1)
        val rangeTitle = formatter.range(sel.period, window.start, rangeEnd)

        val days = if (sel.period == ReportPeriod.DAILY) {
            window.buckets.map { it.start }.filter { !it.isAfter(today) }.reversed().map { d ->
                val suffix = if (d == today) " · ${formatter.todaySuffix()}" else ""
                DayRowUi(d.toEpochDay(), formatter.fullDay(d) + suffix, perDay[d] ?: 0)
            }
        } else emptyList()

        return ReportUiState(
            period = sel.period,
            canGoNewer = sel.offset > 0,
            rangeTitle = rangeTitle,
            total = total,
            averageText = formatter.average(average),
            inGoal = inGoal,
            bars = bars,
            days = days,
            hourly = hourly,
        )
    }

    fun onPeriodSelected(period: ReportPeriod) { selection.value = Selection(period, 0) }
    fun onOlder() = selection.update { it.copy(offset = it.offset + 1) }
    fun onNewer() = selection.update { it.copy(offset = (it.offset - 1).coerceAtLeast(0)) }
}
