package ir.adicom.kamkam.ui.craving

import ir.adicom.kamkam.ui.home.TriggerUi

const val CRAVING_ROUND_SECONDS = 600

data class CravingUiState(
    /** ثانیه‌های باقی‌مانده‌ی دور جاری؛ ۰ یعنی تایمر تمام شده */
    val remainingSeconds: Int,
    /** ثانیه‌ی گذشته از دور جاری، برای هماهنگ‌کردن راهنمای تنفس */
    val elapsedInRound: Int,
    val triggers: List<TriggerUi>,
    val selectedTriggerId: Long?,
) {
    val isFinished: Boolean get() = remainingSeconds <= 0
}

data class CravingActions(
    val onClose: () -> Unit = {},
    val onTriggerSelected: (Long) -> Unit = {},
    val onResisted: () -> Unit = {},
    val onSmoked: () -> Unit = {},
    val onExtend: () -> Unit = {},
)