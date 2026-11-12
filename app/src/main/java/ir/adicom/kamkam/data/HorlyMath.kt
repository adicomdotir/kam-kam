package ir.adicom.kamkam.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId

/** حداقل تعداد نخ در بازه برای اینکه جمله‌ی «بیشتر نخ‌هایت بین ساعت …» نشان داده شود */
const val MIN_HOURLY_INSIGHT_TOTAL = 5

/** تعداد نخ در هر ساعت از شبانه‌روز (۰ تا ۲۳) با منطقه‌ی زمانی کاربر */
@RequiresApi(Build.VERSION_CODES.O)
fun hourCounts(timestamps: List<Long>, zone: ZoneId): IntArray {
    val counts = IntArray(24)
    timestamps.forEach { counts[Instant.ofEpochMilli(it).atZone(zone).hour]++ }
    return counts
}

/**
 * پرتکرارترین بازه‌ی دوساعته‌ی پشت‌سرهم (مثلاً ۱۴ تا ۱۶)؛ null اگر هیچ نخی نیست.
 * در تساوی، بازه‌ی زودتر انتخاب می‌شود. بازه‌ی عبوردهنده از نیمه‌شب (۲۳ تا ۱) در نظر گرفته نمی‌شود.
 */
fun peakWindow(counts: IntArray): Pair<Int, Int>? {
    var best = 0
    var bestStart = -1
    for (h in 0..22) {
        val sum = counts[h] + counts[h + 1]
        if (sum > best) {
            best = sum
            bestStart = h
        }
    }
    return if (bestStart >= 0) bestStart to bestStart + 2 else null
}