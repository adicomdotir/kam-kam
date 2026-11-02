package ir.adicom.kamkam.ui.home

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.entity.Trigger
import java.time.Clock
import java.util.Date
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HomeFormatterModule {
    @Provides
    @Singleton
    fun provideHomeFormatter(
        @ApplicationContext context: Context
    ): HomeFormatter {
        return AndroidHomeFormatter(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideClock(
        @ApplicationContext context: Context
    ): Clock {
        return java.time.Clock.systemDefaultZone()
    }
}

interface HomeFormatter {
    fun date(epochMillis: Long): String
    fun time(epochMillis: Long): String
    fun sinceLast(minutes: Long): String
    fun stage(index: Int): String
    fun triggerLabel(trigger: Trigger): String
}

class AndroidHomeFormatter(private val context: Context) : HomeFormatter {

    // ICU از API 24 به بعد؛ تقویم شمسی بدون کتابخانه‌ی اضافه.
    private val locale = ULocale("fa_IR@calendar=persian")

    override fun date(epochMillis: Long): String =
        SimpleDateFormat("EEEE d MMMM y", locale).format(Date(epochMillis))

    override fun time(epochMillis: Long): String =
        SimpleDateFormat("HH:mm", locale).format(Date(epochMillis))

    override fun sinceLast(minutes: Long): String {
        val h = (minutes / 60).toInt()
        val m = (minutes % 60).toInt()
        return when {
            minutes < 1 -> context.getString(R.string.since_now)
            h == 0 -> context.getString(R.string.since_minutes, m.toFa())
            m == 0 -> context.getString(R.string.since_hours, h.toFa())
            else -> context.getString(R.string.since_hours_minutes, h.toFa(), m.toFa())
        }
    }

    override fun stage(index: Int): String = context.getString(R.string.plan_stage, index.toFa())

    override fun triggerLabel(trigger: Trigger): String {
        trigger.name?.let { return it }
        val res = when (trigger.defaultKey) {
            "stress" -> R.string.trigger_stress
            "coffee" -> R.string.trigger_coffee
            "after_meal" -> R.string.trigger_after_meal
            "social" -> R.string.trigger_social
            "boredom" -> R.string.trigger_boredom
            "driving" -> R.string.trigger_driving
            "waking_up" -> R.string.trigger_waking_up
            else -> R.string.trigger_unknown
        }
        return context.getString(res)
    }
}