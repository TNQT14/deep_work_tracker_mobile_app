package com.deepworktracker.domain.streak

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakCalculatorTest {

    private val today = LocalDate(2026, 8, 10)

    /**
     * Helper: mô tả dữ liệu theo "cách đây mấy ngày" cho dễ đọc.
     * days(0 to 70L, 1 to 80L) = hôm nay 70 phút, hôm qua 80 phút.
     * Ngày KHÔNG liệt kê = không có key trong Map = user không mở app hôm đó.
     */
    private fun days(vararg pairs: Pair<Int, Long>): Map<LocalDate, Long> =
        pairs.associate { (ago, minutes) -> today.minus(ago, DateTimeUnit.DAY) to minutes }

    // ---------- Nhóm 1: hành vi cơ bản ----------

    @Test
    fun `chuoi lien tuc 5 ngay`() {
        val result = StreakCalculator.calculate(
            dailyMinutes = days(0 to 70L, 1 to 70L, 2 to 70L, 3 to 70L, 4 to 70L),
            goalMinutes = 60,
            today = today,
        )
        assertEquals(5, result.current)
        assertEquals(5, result.longest)
        assertTrue(result.isTodayDone)
        assertEquals(70L, result.todayMinutes)
    }

    @Test
    fun `gay o giua thi current va longest khac nhau`() {
        val result = StreakCalculator.calculate(
            // ✅ ✅ ❌(30) ✅ ✅ ✅
            dailyMinutes = days(0 to 70L, 1 to 80L, 2 to 30L, 3 to 90L, 4 to 90L, 5 to 90L),
            goalMinutes = 60,
            today = today,
        )
        assertEquals(2, result.current)
        assertEquals(3, result.longest)
    }

    @Test
    fun `map rong khong crash`() {
        val result = StreakCalculator.calculate(emptyMap(), goalMinutes = 60, today = today)
        assertEquals(StreakResult.EMPTY, result)
    }

    // ---------- Nhóm 2: hai bất biến quan trọng nhất ----------

    /** Hôm nay chưa đạt goal KHÔNG được làm gãy chuỗi. */
    @Test
    fun `hom nay chua dat van giu nguyen chuoi`() {
        val result = StreakCalculator.calculate(
            dailyMinutes = days(0 to 10L, 1 to 70L, 2 to 70L, 3 to 70L),
            goalMinutes = 60,
            today = today,
        )
        assertEquals(3, result.current)      // KHÔNG phải 0
        assertFalse(result.isTodayDone)
        assertEquals(10L, result.todayMinutes)
    }

    /** Hôm nay chưa mở app lần nào — không có key today. Vẫn giữ chuỗi. */
    @Test
    fun `hom nay chua co du lieu van giu nguyen chuoi`() {
        val result = StreakCalculator.calculate(
            dailyMinutes = days(1 to 70L, 2 to 70L),   // thiếu hẳn `today`
            goalMinutes = 60,
            today = today,
        )
        assertEquals(2, result.current)
        assertFalse(result.isTodayDone)
        assertEquals(0L, result.todayMinutes)
    }

    /** Ngày không có key = 0 phút = GÃY. Không được "bỏ qua ngày đó". */
    @Test
    fun `ngay khong mo app lam gay chuoi`() {
        val result = StreakCalculator.calculate(
            dailyMinutes = days(0 to 70L, 1 to 70L, 3 to 70L, 4 to 70L),   // thiếu ngày thứ 2
            goalMinutes = 60,
            today = today,
        )
        assertEquals(2, result.current)
        assertEquals(2, result.longest)      // KHÔNG phải 4
    }

    /** Ngày trống và ngày có data nhưng dưới goal phải cho cùng kết quả. */
    @Test
    fun `ngay trong va ngay duoi goal tuong duong nhau`() {
        val vangMat = StreakCalculator.calculate(
            days(0 to 70L, 1 to 70L, 3 to 70L), goalMinutes = 60, today = today,
        )
        val duoiGoal = StreakCalculator.calculate(
            days(0 to 70L, 1 to 70L, 2 to 5L, 3 to 70L), goalMinutes = 60, today = today,
        )
        assertEquals(vangMat.current, duoiGoal.current)
        assertEquals(vangMat.longest, duoiGoal.longest)
    }

    // ---------- Nhóm 3: biên & trường hợp đặc biệt ----------

    @Test
    fun `dung bang goal duoc tinh la dat`() {
        val result = StreakCalculator.calculate(days(0 to 60L), goalMinutes = 60, today = today)
        assertEquals(1, result.current)
        assertTrue(result.isTodayDone)
    }

    @Test
    fun `goal bang 0 tra ve empty nhung giu todayMinutes`() {
        val result = StreakCalculator.calculate(days(0 to 100L), goalMinutes = 0, today = today)
        assertEquals(0, result.current)
        assertEquals(0, result.longest)
        assertFalse(result.isTodayDone)
        assertEquals(100L, result.todayMinutes)
    }

    @Test
    fun `ky luc trong qua khu lon hon chuoi hien tai`() {
        val result = StreakCalculator.calculate(
            // hôm nay ✅, hôm qua ❌, rồi 4 ngày ✅ liên tiếp
            dailyMinutes = days(0 to 70L, 1 to 10L, 2 to 70L, 3 to 70L, 4 to 70L, 5 to 70L),
            goalMinutes = 60,
            today = today,
        )
        assertEquals(1, result.current)
        assertEquals(4, result.longest)
    }

    @Test
    fun `longest khong bao gio nho hon current`() {
        val result = StreakCalculator.calculate(
            dailyMinutes = days(0 to 70L, 1 to 70L, 2 to 70L),
            goalMinutes = 60,
            today = today,
        )
        assertEquals(result.current, result.longest)
    }

    // ---------- Nhóm 4: tương thích ngược với computeStreaks() cũ ----------

    /**
     * Bản sao nguyên văn logic computeStreaks() cũ (đã bị xoá khỏi
     * GoalDetailViewModel và CategoryDetailViewModel), dùng làm mốc đối chiếu.
     */
    private fun legacyComputeStreaks(dailyMinutes: List<Long>): Pair<Int, Int> {
        var current = 0
        var best = 0
        for (m in dailyMinutes) {
            if (m > 0) {
                current++
                if (current > best) best = current
            } else {
                current = 0
            }
        }
        var suffix = 0
        for (i in dailyMinutes.indices.reversed()) {
            if (dailyMinutes[i] > 0) suffix++ else break
        }
        return suffix to best
    }

    /**
     * Chứng minh phép quy đổi `if (isTodayDone) current else 0` tái hiện đúng
     * hành vi cũ — đây là thứ cho phép xoá computeStreaks() mà không đổi con số
     * hiển thị trên Goal Detail / Category Detail.
     */
    @Test
    fun `tuong thich nguoc voi computeStreaks cu`() {
        // Mỗi case là dãy phút từ NGÀY CŨ NHẤT tới NGÀY MỚI NHẤT (dày, có cả số 0).
        val cases = listOf(
            listOf(70L, 70L, 70L, 70L, 70L),          // liên tục
            listOf(90L, 90L, 90L, 0L, 80L, 70L),      // gãy giữa
            listOf(70L, 70L, 70L, 0L),                // ngày cuối trống — case quan trọng nhất
            listOf(0L, 0L, 0L),                       // toàn trống
            listOf(0L, 70L, 0L, 70L, 70L),            // xen kẽ
            listOf(70L),                              // một ngày
        )

        cases.forEach { dense ->
            val endDate = today
            val startDate = today.minus(dense.size - 1, DateTimeUnit.DAY)
            // Dựng Map bằng cách BỎ các ngày 0 phút — mô phỏng đúng thứ mà
            // SessionDao.getDailyFocusTotals() (GROUP BY date) trả về.
            val map = dense.mapIndexedNotNull { i, minutes ->
                if (minutes > 0) startDate.plus(i, DateTimeUnit.DAY) to minutes else null
            }.toMap()

            val streak = StreakCalculator.calculate(map, goalMinutes = 1, today = endDate)
            val actual = (if (streak.isTodayDone) streak.current else 0) to streak.longest

            assertEquals("dãy $dense", legacyComputeStreaks(dense), actual)
        }
    }
}
