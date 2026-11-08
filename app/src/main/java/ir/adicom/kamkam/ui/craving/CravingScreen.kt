package ir.adicom.kamkam.ui.craving

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.adicom.kamkam.R
import ir.adicom.kamkam.ui.components.TriggerChips
import ir.adicom.kamkam.ui.home.TriggerUi
import ir.adicom.kamkam.ui.home.toFa
import ir.adicom.kamkam.ui.theme.KamkamTheme

@Composable
fun CravingScreen(
    state: CravingUiState,
    actions: CravingActions,
    modifier: Modifier = Modifier,
) {
    // تا وقتی این صفحه باز است صفحه‌نمایش خاموش نشود
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
            IconButton(onClick = actions.onClose) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.edit_close))
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(if (state.isFinished) R.string.craving_finished_title else R.string.craving_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    stringResource(if (state.isFinished) R.string.craving_finished_body else R.string.craving_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            TimerRing(progress = state.remainingSeconds / CRAVING_ROUND_SECONDS.toFloat()) {
                if (state.isFinished) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            formatClock(state.remainingSeconds),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            stringResource(R.string.craving_time_left),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (!state.isFinished) {
                BreathingCard(phaseSecond = state.elapsedInRound % BREATH_CYCLE)

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.craving_trigger_question),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TriggerChips(state.triggers, state.selectedTriggerId, actions.onTriggerSelected)
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Button(onClick = actions.onResisted, modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape) {
                Text(stringResource(R.string.craving_resisted), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (state.isFinished) {
                OutlinedButton(onClick = actions.onExtend, modifier = Modifier.fillMaxWidth().height(48.dp), shape = CircleShape) {
                    Text(stringResource(R.string.craving_extend))
                }
            }
            TextButton(onClick = actions.onSmoked, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(stringResource(R.string.craving_smoked), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TimerRing(progress: Float, content: @Composable () -> Unit) {
    val animated by animateFloatAsState(progress, tween(1_000, easing = LinearEasing), label = "ring")
    val track = MaterialTheme.colorScheme.surfaceVariant
    val color = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(track, 0f, 360f, false, topLeft, arcSize, style = Stroke(stroke))
            drawArc(color, -90f, 360f * animated, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        content()
    }
}

/** چرخه‌ی تنفس: ۴ ثانیه دم، ۲ ثانیه نگه‌داشتن، ۶ ثانیه بازدم */
private const val BREATH_CYCLE = 12

@Composable
private fun BreathingCard(phaseSecond: Int) {
    val labelRes = when {
        phaseSecond < 4 -> R.string.craving_breath_in
        phaseSecond < 6 -> R.string.craving_breath_hold
        else -> R.string.craving_breath_out
    }
    // مقدار انتهای همین ثانیه؛ انیمیشن ۱ ثانیه‌ای با ثانیه‌شمار هماهنگ می‌ماند.
    val target = when {
        phaseSecond < 4 -> 1f + (phaseSecond + 1) / 4f * 0.6f
        phaseSecond < 6 -> 1.6f
        else -> 1.6f - (phaseSecond - 5) / 6f * 0.6f
    }
    val scale by animateFloatAsState(target, tween(1_000, easing = LinearEasing), label = "breath")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
            }
            Column {
                Text(stringResource(labelRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(
                    stringResource(R.string.craving_breath_pattern),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatClock(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "${m.toFa().padStart(2, '۰')}:${s.toFa().padStart(2, '۰')}"
}

private val sampleTriggers = listOf(TriggerUi(1, "استرس"), TriggerUi(2, "قهوه"), TriggerUi(5, "بی‌حوصلگی"), TriggerUi(3, "بعد غذا"))
private const val SPEC = "spec:width=390dp,height=844dp,dpi=420"

@Preview(name = "در جریان", device = SPEC, showBackground = true)
@Composable
private fun CravingRunningPreview() = KamkamTheme {
    CravingScreen(CravingUiState(358, 242, sampleTriggers, 5), CravingActions())
}

@Preview(name = "پایان تایمر", device = SPEC, showBackground = true)
@Composable
private fun CravingFinishedPreview() = KamkamTheme {
    CravingScreen(CravingUiState(0, 600, sampleTriggers, null), CravingActions())
}