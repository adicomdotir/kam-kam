package ir.adicom.kamkam.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.adicom.kamkam.ui.theme.PurpleDark
import ir.adicom.kamkam.ui.theme.PurpleLight

@Composable
fun TrainingFour() {


    Scaffold(
        topBar = {
            Column {
                Row(
                    Modifier
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, "")
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PurpleLight, CircleShape)
                            .border(1.dp, PurpleDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S", color = PurpleDark, fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column() {
                        Text("Sara", fontSize = 16.sp)
                        Text("Online", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                HorizontalDivider()
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            BubbleCard(
                msg = "Hi! Are you free today?",
                isLast = false
            )
            BubbleCard(
                msg = "Want to grab lunch?",
            )
            BubbleCard(
                msg = "Sure, what time?",
                isLast = false,
                isLeft = false,
            )
            BubbleCard(
                msg = "12:30 works for me",
                isLeft = false,
            )
            BubbleCard(
                msg = "Perfect, see you there",
            )
        }
    }
}

@Composable
fun BubbleCard(
    msg: String,
    isLeft: Boolean = true,
    isLast: Boolean = true,
) {
    val density = LocalDensity.current
    val windowWidthPx = LocalWindowInfo.current.containerSize.width
    val windowWidthDp = with(density) { (windowWidthPx * 0.8).toInt().toDp() }

    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = if (isLeft) Alignment.Start else Alignment.End
    ) {
        Box(
            Modifier
                .widthIn(max = windowWidthDp)
                .height(40.dp)
                .background(
                    if(isLeft) Color(0x7FA8A7A1) else Color(0x7F80A8D1),
                    RoundedCornerShape(
                        topEnd = 16.dp,
                        topStart = 16.dp,
                        bottomStart = if (isLeft && isLast) 4.dp else 16.dp,
                        bottomEnd = if (!isLeft && isLast) 4.dp else 16.dp
                    )
                )
                .border(
                    2.dp,
                    if(isLeft) Color(0xFFA8A7A1) else Color(0xFF80A8D1),
                    RoundedCornerShape(
                        topEnd = 16.dp,
                        topStart = 16.dp,
                        bottomStart = if (isLeft && isLast) 4.dp else 16.dp,
                        bottomEnd = if (!isLeft && isLast) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Text(msg)
        }
        Spacer(Modifier.height(if (isLast) 16.dp else 8.dp))
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingFourPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrainingFour()
    }
}