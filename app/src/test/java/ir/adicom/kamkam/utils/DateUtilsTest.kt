package ir.adicom.kamkam.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Unit tests for [DateUtils].
 *
 * The production code reads the timestamp through [Calendar.getInstance], which uses the
 * JVM default time zone. The tests therefore never hard-code epoch millis for a date;
 * [atNoon] builds the instant from a Gregorian calendar date so the expectation stays
 * valid whatever the default zone is.
 */
class DateUtilsTest {

    /** Jalali month names in calendar order (Farvardin .. Esfand). */
    private val monthNames = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر",
        "مرداد", "شهریور", "مهر", "آبان",
        "آذر", "دی", "بهمن", "اسفند"
    )

    /**
     * Epoch millis for 12:00 local time on the given Gregorian date.
     * Midday is used on purpose: any timestamp on the same local day yields the same
     * Jalali date, so the assertion does not depend on the hour.
     */
    private fun atNoon(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(year, month - 1, day, 12, 0, 0)
        return calendar.timeInMillis
    }

    /** Epoch millis for 12:00 UTC on the given Gregorian date, independent of the default zone. */
    private fun utcNoon(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.clear()
        calendar.set(year, month - 1, day, 12, 0, 0)
        return calendar.timeInMillis
    }

    // ---------------------------------------------------------------------
    // convertTimestampToShamsi
    // ---------------------------------------------------------------------

    @Test
    fun `converts a mid-year date to shamsi`() {
        // 15 December 2023 == 24 Azar 1402
        assertEquals("1402/09/24", DateUtils.convertTimestampToShamsi(atNoon(2023, 12, 15)))
    }

    @Test
    fun `converts nowruz to first day of farvardin`() {
        // 20 March 2024 == 1 Farvardin 1403
        assertEquals("1403/01/01", DateUtils.convertTimestampToShamsi(atNoon(2024, 3, 20)))
        // the following day is 2 Farvardin
        assertEquals("1403/01/02", DateUtils.convertTimestampToShamsi(atNoon(2024, 3, 21)))
    }

    @Test
    fun `converts the day before nowruz to the last day of esfand`() {
        // 19 March 2024 == 29 Esfand 1402
        assertEquals("1402/12/29", DateUtils.convertTimestampToShamsi(atNoon(2024, 3, 19)))
    }

    @Test
    fun `converts dates in the second half of the year`() {
        // 1 January 2024 == 11 Dey 1402
        assertEquals("1402/10/11", DateUtils.convertTimestampToShamsi(atNoon(2024, 1, 1)))
        // 1 June 2025 == 11 Khordad 1404
        assertEquals("1404/03/11", DateUtils.convertTimestampToShamsi(atNoon(2025, 6, 1)))
    }

    @Test
    fun `converts historical dates`() {
        // 11 February 1979 == 22 Bahman 1357 (Islamic Revolution)
        assertEquals("1357/11/22", DateUtils.convertTimestampToShamsi(atNoon(1979, 2, 11)))
        // 1 January 2000 == 11 Dey 1378
        assertEquals("1378/10/11", DateUtils.convertTimestampToShamsi(atNoon(2000, 1, 1)))
    }

    @Test
    fun `zero pads month and day to two digits`() {
        val formatted = DateUtils.convertTimestampToShamsi(atNoon(2024, 3, 20))
        val parts = formatted.split("/")
        assertEquals(3, parts.size)
        assertEquals(4, parts[0].length)
        assertEquals(2, parts[1].length)
        assertEquals(2, parts[2].length)
    }

    @Test
    fun `result is always a valid shamsi date`() {
        // walk a full Gregorian year day by day
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(2024, Calendar.JANUARY, 1, 12, 0, 0)
        repeat(366) {
            val shamsi = DateUtils.convertTimestampToShamsi(calendar.timeInMillis)
            val parts = shamsi.split("/")
            assertEquals("unparsable result: $shamsi", 3, parts.size)

            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            assertTrue("year out of range in $shamsi", year in 1300..1500)
            assertTrue("month out of range in $shamsi", month in 1..12)
            assertTrue("day out of range in $shamsi", day in 1..31)
            // first six months have 31 days, the rest at most 30
            if (month <= 6) {
                assertTrue("day $day invalid for month $month", day in 1..31)
            } else {
                assertTrue("day $day invalid for month $month", day in 1..30)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    @Test
    fun `consecutive days produce consecutive shamsi dates`() {
        // A day-by-day walk must advance the Jalali date by exactly one day, and every
        // month must have its fixed length (31 for the first six, 30 for the next five,
        // 29 or 30 for Esfand). Walking 400 days crosses at least one year boundary.
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(2024, Calendar.JANUARY, 1, 12, 0, 0)

        var previous = parse(DateUtils.convertTimestampToShamsi(calendar.timeInMillis))

        repeat(400) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val current = parse(DateUtils.convertTimestampToShamsi(calendar.timeInMillis))

            if (current.year == previous.year && current.month == previous.month) {
                assertEquals(
                    "day did not advance: $previous -> $current",
                    previous.day + 1,
                    current.day
                )
            } else {
                // a new month always starts at day 1
                assertEquals("month did not restart at 1: $previous -> $current", 1, current.day)

                val expectedLength = when {
                    previous.month <= 6 -> 31
                    previous.month <= 11 -> 30
                    else -> null // Esfand: 29 or 30 depending on the leap year
                }
                if (expectedLength != null) {
                    assertEquals(
                        "wrong length for month ${previous.month} ($previous)",
                        expectedLength,
                        previous.day
                    )
                } else {
                    assertTrue(
                        "Esfand had an invalid length in $previous",
                        previous.day == 29 || previous.day == 30
                    )
                    if (previous.day == 30) {
                        // a year whose Esfand has 30 days is 366 days long
                        assertEquals(
                            "only leap years may end with Esfand 30",
                            30,
                            previous.day
                        )
                    }
                }
            }
            previous = current
        }
    }

    @Test
    fun `a leap shamsi year lasts 366 days`() {
        // 1403 is a leap year, so it ends with Esfand 30 and is 366 days long.
        val dayMillis = java.util.concurrent.TimeUnit.DAYS.toMillis(1)
        val firstOf1403 = utcNoon(2024, 3, 20)

        assertEquals("1403/01/01", DateUtils.convertTimestampToShamsi(firstOf1403))
        // the 365th day after 1 Farvardin is the extra day, Esfand 30
        assertEquals(
            "1403/12/30",
            DateUtils.convertTimestampToShamsi(firstOf1403 + 365 * dayMillis)
        )
        // and the year after starts 366 days later
        assertEquals(
            "1404/01/01",
            DateUtils.convertTimestampToShamsi(firstOf1403 + 366 * dayMillis)
        )
        // which is 21 March 2025
        assertEquals(
            "1404/01/01",
            DateUtils.convertTimestampToShamsi(utcNoon(2025, 3, 21))
        )
    }

    private data class ShamsiDate(val year: Int, val month: Int, val day: Int) {
        // built by hand so the message does not depend on the default locale
        override fun toString() = "$year/$month/$day"
    }

    private fun parse(shamsi: String): ShamsiDate {
        val (year, month, day) = shamsi.split("/").map(String::toInt)
        return ShamsiDate(year, month, day)
    }

    // ---------------------------------------------------------------------
    // convertTimestampToShamsiWithMonthName
    // ---------------------------------------------------------------------

    @Test
    fun `formats date with the persian month name`() {
        // 15 December 2023 == 24 Azar 1402
        assertEquals(
            "24 آذر 1402",
            DateUtils.convertTimestampToShamsiWithMonthName(atNoon(2023, 12, 15))
        )
    }

    @Test
    fun `uses the correct month name for every month of the year`() {
        // The 21st of each Jalali month of the year 1403, expressed as Gregorian dates.
        val gregorianDates = listOf(
            GregorianDate(2024, 4, 9), GregorianDate(2024, 5, 10), GregorianDate(2024, 6, 10),
            GregorianDate(2024, 7, 11), GregorianDate(2024, 8, 11), GregorianDate(2024, 9, 11),
            GregorianDate(2024, 10, 12), GregorianDate(2024, 11, 11), GregorianDate(2024, 12, 11),
            GregorianDate(2025, 1, 10), GregorianDate(2025, 2, 9), GregorianDate(2025, 3, 11)
        )
        gregorianDates.forEachIndexed { index, (year, month, day) ->
            val shamsi = DateUtils.convertTimestampToShamsiWithMonthName(atNoon(year, month, day))
            assertEquals(
                "month index $index (${monthNames[index]})",
                "21 ${monthNames[index]} 1403",
                shamsi
            )
        }
    }

    private data class GregorianDate(val year: Int, val month: Int, val day: Int)

    @Test
    fun `month name output contains day, name and year`() {
        val result = DateUtils.convertTimestampToShamsiWithMonthName(atNoon(2024, 3, 20))
        val parts = result.split(" ")
        assertEquals(3, parts.size)
        // the day keeps the zero padding of the numeric format
        assertEquals("01", parts[0])
        assertEquals("فروردین", parts[1])
        assertEquals("1403", parts[2])
    }

    // ---------------------------------------------------------------------
    // getMonthYearShamsi
    // ---------------------------------------------------------------------

    @Test
    fun `formats month name and year without the day`() {
        // 15 December 2023 == Azar 1402
        assertEquals("آذر 1402", DateUtils.getMonthYearShamsi(atNoon(2023, 12, 15)))
        // 20 March 2024 == Farvardin 1403
        assertEquals("فروردین 1403", DateUtils.getMonthYearShamsi(atNoon(2024, 3, 20)))
    }

    @Test
    fun `month year output never contains the day number`() {
        val result = DateUtils.getMonthYearShamsi(atNoon(2024, 1, 1))
        val parts = result.split(" ")
        assertEquals(2, parts.size)
        assertTrue("expected a known month name in '$result'", monthNames.contains(parts[0]))
        assertEquals("1402", parts[1])
    }

    // ---------------------------------------------------------------------
    // getCurrentDateShamsi
    // ---------------------------------------------------------------------

    @Test
    fun `current date matches the conversion of the current timestamp`() {
        assertEquals(
            DateUtils.convertTimestampToShamsi(System.currentTimeMillis()),
            DateUtils.getCurrentDateShamsi()
        )
    }

    @Test
    fun `current date has the shamsi shape`() {
        val current = DateUtils.getCurrentDateShamsi()
        assertTrue(
            "unexpected format: $current",
            Regex("""^\d{4}/\d{2}/\d{2}$""").matches(current)
        )
    }

    // ---------------------------------------------------------------------
    // getMonthStartTimestamp / getMonthEndTimestamp
    // ---------------------------------------------------------------------

    @Test
    fun `month start is midnight on the first day`() {
        val timestamp = DateUtils.getMonthStartTimestamp(2024, 3)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        assertEquals(2024, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH))
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, calendar.get(Calendar.MINUTE))
        assertEquals(0, calendar.get(Calendar.SECOND))
        assertEquals(0, calendar.get(Calendar.MILLISECOND))
    }

    @Test
    fun `month end is the last millisecond of the last day`() {
        val timestamp = DateUtils.getMonthEndTimestamp(2024, 2)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        assertEquals(2024, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH))
        // 2024 is a leap year, so February ends on the 29th
        assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, calendar.get(Calendar.MINUTE))
        assertEquals(59, calendar.get(Calendar.SECOND))
        assertEquals(999, calendar.get(Calendar.MILLISECOND))
    }

    @Test
    fun `month end handles a 31 day month`() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = DateUtils.getMonthEndTimestamp(2024, 1)

        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH))
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `month end handles a 30 day month`() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = DateUtils.getMonthEndTimestamp(2024, 4)

        assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH))
        assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `month start is before month end and the range covers the whole month`() {
        val start = DateUtils.getMonthStartTimestamp(2024, 3)
        val end = DateUtils.getMonthEndTimestamp(2024, 3)

        assertTrue("start must be before end", start < end)
        // 31 days minus one millisecond
        assertEquals(31L * 24 * 60 * 60 * 1000 - 1, end - start)
    }

    @Test
    fun `month start of the next month equals month end plus one millisecond`() {
        val marchEnd = DateUtils.getMonthEndTimestamp(2024, 3)
        val aprilStart = DateUtils.getMonthStartTimestamp(2024, 4)

        assertEquals(marchEnd + 1, aprilStart)
    }

    @Test
    fun `december rolls over to the next year`() {
        val decemberEnd = DateUtils.getMonthEndTimestamp(2024, 12)
        val januaryStart = DateUtils.getMonthStartTimestamp(2025, 1)

        assertEquals(decemberEnd + 1, januaryStart)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = decemberEnd
        assertEquals(2024, calendar.get(Calendar.YEAR))
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH))
        assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH))
    }

    // ---------------------------------------------------------------------
    // locale independence
    // ---------------------------------------------------------------------

    @Test
    fun `output does not use locale specific digits`() {
        val default = Locale.getDefault()
        try {
            // A locale with Arabic-Indic digits must not change the output format.
            Locale.setDefault(Locale.forLanguageTag("ar-EG"))
            val result = DateUtils.convertTimestampToShamsi(atNoon(2024, 3, 20))
            assertEquals("1403/01/01", result)
            assertFalse("latin digits expected", result.any { it.code > 127 })
        } finally {
            Locale.setDefault(default)
        }
    }

    @Test
    fun `output depends on the time zone of the same instant`() {
        // The same instant is a different local day depending on the zone, because the
        // Gregorian date is read through a Calendar.
        val instant = utcNoon(2024, 3, 20)

        assertEquals(
            "1403/01/01",
            DateUtils.convertTimestampToShamsi(instant, TimeZone.getTimeZone("UTC"))
        )
        assertEquals(
            "1403/01/01",
            DateUtils.convertTimestampToShamsi(instant, TimeZone.getTimeZone("Asia/Tehran"))
        )
        // UTC+14 is already on the next day at 12:00 UTC
        assertEquals(
            "1403/01/02",
            DateUtils.convertTimestampToShamsi(instant, TimeZone.getTimeZone("Pacific/Kiritimati"))
        )
    }

    @Test
    fun `single argument overload uses the default time zone`() {
        val instant = utcNoon(2024, 3, 20)
        assertEquals(
            DateUtils.convertTimestampToShamsi(instant, TimeZone.getDefault()),
            DateUtils.convertTimestampToShamsi(instant)
        )
    }

    @Test
    fun `time zone does not change the instant, only its rendering`() {
        // 12:00 UTC is still 20 March in Tehran (UTC+3:30) and in UTC.
        val instant = utcNoon(2024, 3, 20)
        val tehran = DateUtils.convertTimestampToShamsi(instant, TimeZone.getTimeZone("Asia/Tehran"))
        val utc = DateUtils.convertTimestampToShamsi(instant, TimeZone.getTimeZone("UTC"))

        assertEquals(tehran, utc)
    }
}
