package ir.adicom.kamkam.ui.practice

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.adicom.kamkam.ui.theme.PurpleDark
import ir.adicom.kamkam.ui.theme.PurpleLight

data class Message(val text: String, val isMine: Boolean)

@Composable
fun TrainingFour() {
    var input by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("Hi! Are you free today?", false),
            Message("Want to grab lunch?", false),
            Message("Sure, what time?", true),
            Message("12:30 works for me", true),
            Message("Perfect, see you there", false),
        )
    }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.lastIndex)
    }

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
        },
        bottomBar = {
            Column(Modifier.navigationBarsPadding().imePadding()) {
                HorizontalDivider()
                Row(
                    Modifier.fillMaxWidth().height(64.dp).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = LocalContentColor.current
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .border(1.dp, Color.LightGray, CircleShape),
                        decorationBox = { inner ->
                            Box(
                                Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (input.isEmpty()) Text("Message", color = Color.Gray, fontSize = 14.sp)
                                inner()
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                messages.add(Message(input.trim(), true))
                                input = ""
                            }
                        },
                        modifier = Modifier.size(40.dp).background(Color(0xFF80A8D1), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(messages) { i, m ->
                val prev = messages.getOrNull(i - 1)
                val next = messages.getOrNull(i + 1)
                BubbleCard(
                    msg = m.text,
                    isMine = m.isMine,
                    isLast = next == null || next.isMine != m.isMine,
                    modifier = Modifier.padding(
                        top = when {
                            prev == null -> 0.dp
                            prev.isMine != m.isMine -> 16.dp
                            else -> 8.dp
                        }
                    )
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BubbleCard(msg: String, isMine: Boolean, isLast: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val shape = RoundedCornerShape(
            topStart = 16.dp, topEnd = 16.dp,
            bottomStart = if (!isMine && isLast) 4.dp else 16.dp,
            bottomEnd = if (isMine && isLast) 4.dp else 16.dp
        )
        Box(
            Modifier
                .align(if (isMine) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = maxWidth * 0.75f)
                .background(if (isMine) Color(0x7F80A8D1) else Color(0x7FA8A7A1), shape)
                .border(1.dp, if (isMine) Color(0xFF80A8D1) else Color(0xFFA8A7A1), shape)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(msg, fontSize = 15.sp)
        }
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