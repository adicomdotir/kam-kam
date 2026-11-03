package ir.adicom.kamkam.ui.plan

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.adicom.kamkam.R
import ir.adicom.kamkam.data.PlanDraft
import ir.adicom.kamkam.ui.home.toFa
import ir.adicom.kamkam.ui.theme.KamkamTheme

@Composable
fun CreatePlanScreen(
    state: CreatePlanUiState,
    actions: CreatePlanActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(onClick = actions.onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.plan_back))
            }
            Text(stringResource(R.string.plan_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                stringResource(R.string.plan_intro),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            StepperCard(
                label = stringResource(R.string.plan_baseline),
                hint = state.baselineHintDays?.let { stringResource(R.string.plan_baseline_hint_data, it.toFa()) }
                    ?: stringResource(R.string.plan_baseline_hint_manual),
                stepper = state.baseline,
                onDelta = actions.onBaselineDelta,
            )
            StepperCard(
                label = stringResource(R.string.plan_step_size),
                hint = stringResource(R.string.plan_step_size_hint),
                stepper = state.stepSize,
                onDelta = actions.onStepSizeDelta,
            )
            StepperCard(
                label = stringResource(R.string.plan_step_days),
                hint = stringResource(R.string.plan_step_days_hint),
                stepper = state.stepDays,
                onDelta = actions.onStepDaysDelta,
            )
            StepperCard(
                label = stringResource(R.string.plan_final_target),
                hint = stringResource(R.string.plan_final_target_hint),
                stepper = state.finalTarget,
                onDelta = actions.onFinalTargetDelta,
            )
            PreviewCard(state.preview, state.weeksToGoal, Modifier.padding(top = 4.dp, bottom = 8.dp))
        }

        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)) {
                    Text(
                        stringResource(R.string.plan_extend_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    )
                    Button(onClick = actions.onStart, modifier = Modifier.fillMaxWidth().height(56.dp), shape = CircleShape) {
                        Text(stringResource(R.string.plan_start), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperCard(
    label: String,
    hint: String,
    stepper: StepperUi,
    onDelta: (Int) -> Unit,
) {
    val decDesc = stringResource(R.string.plan_decrease, label)
    val incDesc = stringResource(R.string.plan_increase, label)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedIconButton(
                    onClick = { onDelta(-1) },
                    enabled = stepper.canDecrement,
                    modifier = Modifier.semantics { contentDescription = decDesc },
                ) { Text("−", style = MaterialTheme.typography.titleLarge) }
                Text(
                    stepper.value.toFa(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(44.dp),
                )
                FilledTonalIconButton(
                    onClick = { onDelta(1) },
                    enabled = stepper.canIncrement,
                    modifier = Modifier.semantics { contentDescription = incDesc },
                ) { Text("+", style = MaterialTheme.typography.titleLarge) }
            }
        }
    }
}

@Composable
private fun PreviewCard(items: List<PreviewItem>, weeks: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.plan_preview), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.plan_weeks_to_goal, weeks.toFa()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items.forEach { item ->
                when (item) {
                    PreviewItem.Gap -> Text(
                        "⋯",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    is PreviewItem.Stage -> Row(
                        modifier = Modifier.fillMaxWidth().height(28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            stringResource(R.string.plan_stage_row, item.index.toFa()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(64.dp),
                        )
                        LinearProgressIndicator(
                            progress = { item.fraction },
                            modifier = Modifier.weight(1f).height(10.dp),
                            color = if (item.isCurrent) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                            trackColor = MaterialTheme.colorScheme.background,
                            strokeCap = StrokeCap.Round,
                        )
                        Text(
                            item.target.toFa(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width(24.dp),
                        )
                    }
                }
            }
        }
    }
}

private const val SPEC = "spec:width=390dp,height=844dp,dpi=420"

@Preview(name = "عادی", device = SPEC, showBackground = true)
@Composable
private fun CreatePlanPreview() = KamkamTheme {
    CreatePlanScreen(PlanDraft(baseline = 8).normalized().toUiState(baselineHintDays = 7), CreatePlanActions())
}

@Preview(name = "تیره، بدون داده", device = SPEC, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CreatePlanDarkPreview() = KamkamTheme {
    CreatePlanScreen(PlanDraft(baseline = 10, stepSize = 2).normalized().toUiState(baselineHintDays = null), CreatePlanActions())
}