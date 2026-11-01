package ir.adicom.kamkam.utils

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    /** نام ماه‌های شمسی به ترتیب تقویم */
    private val SHAMSI_MONTHS = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر",
        "مرداد", "شهریور", "مهر", "آبان",
        "آذر", "دی", "بهمن", "اسفند"
    )

    /**
     * تبدیل Timestamp (میلادی) به تاریخ شمسی
     * مثال: 1704067200000 → "1402/09/24"
     */
    fun convertTimestampToShamsi(timestamp: Long): String =
        convertTimestampToShamsi(timestamp, TimeZone.getDefault())

    /**
     * مانند [convertTimestampToShamsi] اما با منطقه زمانی مشخص.
     * یک لحظه (Timestamp) در منطقه‌های زمانی مختلف می‌تواند روز متفاوتی باشد.
     */
    fun convertTimestampToShamsi(timestamp: Long, timeZone: TimeZone): String {
        val calendar = Calendar.getInstance(timeZone)
        calendar.timeInMillis = timestamp

        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)

        // شماره روز مطلق میلادی نسبت به 1600/01/01
        val gMonthDayNo = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)[gm - 1]
        val isGLeapYear = ((gy % 4 == 0) && (gy % 100 != 0)) || (gy % 400 == 0)

        var gDayNo = 365L * (gy - 1600) + ((gy - 1600 + 3) / 4) -
                ((gy - 1600 + 99) / 100) + ((gy - 1600 + 399) / 400)
        // روزهای گذشته از سال میلادی (ماه ژانویه = 0)
        gDayNo += (gMonthDayNo + gd - 1).toLong()
        if (gm > 2 && isGLeapYear) {
            gDayNo++
        }

        // تبدیل به شماره روز شمسی
        var jDayNo = gDayNo - 79

        val jy2 = 33 * (jDayNo / 12053)
        jDayNo %= 12053

        var jy = 979 + jy2 + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        val resultJm: Int
        val resultJd: Int
        if (jDayNo < 186) {
            // شش ماه اول سال: هر ماه ۳۱ روز
            resultJm = 1 + (jDayNo / 31).toInt()
            resultJd = 1 + (jDayNo % 31).toInt()
        } else {
            // شش ماه دوم سال: هر ماه ۳۰ روز
            resultJm = 7 + ((jDayNo - 186) / 30).toInt()
            resultJd = 1 + ((jDayNo - 186) % 30).toInt()
        }

        return String.format(Locale.US, "%04d/%02d/%02d", jy, resultJm, resultJd)
    }

    /**
     * تبدیل Timestamp به فرمت شمسی با نام ماه
     * مثال: 1704067200000 → "24 آذر 1402"
     */
    fun convertTimestampToShamsiWithMonthName(timestamp: Long): String {
        val shamsiDate = convertTimestampToShamsi(timestamp)
        val parts = shamsiDate.split("/")
        if (parts.size != 3) return shamsiDate

        val day = parts[2]
        val monthName = monthNameOf(parts[1])
        val year = parts[0]

        return "$day $monthName $year"
    }

    /**
     * بدست آوردن تاریخ جاری به صورت شمسی
     */
    fun getCurrentDateShamsi(): String {
        return convertTimestampToShamsi(System.currentTimeMillis())
    }

    /**
     * تبدیل Timestamp به نام ماه و سال شمسی
     * مثال: 1704067200000 → "آذر 1402"
     */
    fun getMonthYearShamsi(timestamp: Long): String {
        val shamsiDate = convertTimestampToShamsi(timestamp)
        val parts = shamsiDate.split("/")
        if (parts.size != 3) return shamsiDate

        return "${monthNameOf(parts[1])} ${parts[0]}"
    }

    /** نام ماه شمسی از شماره ماه یک‌رقمی یا دو‌رقمی، مثلا "09" → "آذر" */
    private fun monthNameOf(month: String): String {
        val monthIndex = month.toIntOrNull()?.minus(1) ?: return "نامشخص"
        return SHAMSI_MONTHS.getOrElse(monthIndex) { "نامشخص" }
    }

    /**
     * بدست آوردن ابتدای ماه (Timestamp)
     * توجه: [year] و [month] میلادی هستند (ماه از ۱ شروع می‌شود).
     */
    fun getMonthStartTimestamp(year: Int, month: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * بدست آوردن انتهای ماه (Timestamp)
     * توجه: [year] و [month] میلادی هستند (ماه از ۱ شروع می‌شود).
     */
    fun getMonthEndTimestamp(year: Int, month: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 0, 23, 59, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}