package ir.adicom.kamkam.ui.practice

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.adicom.kamkam.ui.practice.Transaction
import ir.adicom.kamkam.ui.theme.PurpleDark
import ir.adicom.kamkam.ui.theme.PurpleLight

@Composable
fun TrainingFive() {
    Scaffold(
        topBar = {
            FiveTopBar()
        },
        bottomBar = {
            FiveBottomBar()
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Box(
                    Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .background(PurpleLight, RoundedCornerShape(20.dp))
                        .border(2.dp, PurpleDark, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column() {
                        Text("Total balance", color = PurpleDark, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "$ 12,450.20",
                            color = PurpleDark,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .size(width = 64.dp, height = 24.dp)
                                .background(Color.Green, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "+4.2%",
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                QuickItems()
            }
            item {
                Spacer(Modifier.height(16.dp))
                SpendingChart()
                Spacer(Modifier.height(16.dp))
            }
            items(transactions) {
                TransactionRow(it)
            }
        }
    }
}

data class Transaction(val title: String, val date: String, val price: String)

val transactions = listOf<Transaction>(
    Transaction(
        title = "Coffee shop",
        date = "Today, 9:40",
        price = "-$4.5"
    ),
    Transaction(
        title = "Coffee shop",
        date = "Today, 9:40",
        price = "-$4.5"
    ),
    Transaction(
        title = "Salary",
        date = "Yesterday",
        price = "+$4500"
    )
)

@Composable
fun TransactionRow(transaction: Transaction) {
    Row(
        Modifier.height(64.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(PurpleLight, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1.0f)) {
            Text(transaction.title, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text(transaction.date, fontSize = 12.sp)
        }
        Text(transaction.price, fontSize = 15.sp)
    }

}

@Composable
fun QuickItems() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickItem("Send")
        QuickItem("Request")
        QuickItem("Top up")
        QuickItem("More")
        QuickItem("Top up")
        QuickItem("More")
    }
}

@Composable
fun QuickItem(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(PurpleLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.FavoriteBorder, "", modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, fontSize = 12.sp)
    }
}

@Composable
fun FiveTopBar() {
    Row(
        Modifier
            .height(40.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(PurpleLight, CircleShape)
                .border(2.dp, PurpleDark, CircleShape), contentAlignment = Alignment.Center
        ) {
            Text("A", fontSize = 12.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1.0f)) {
            Text("Good Morning", fontSize = 12.sp)
            Text("Ali", fontSize = 18.sp)
        }
        Box(
            Modifier
                .size(40.dp)
                .border(2.dp, Color.Black, CircleShape), contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, "")
            Badge(
                Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun FiveBottomBar() {
    NavigationBar(
        Modifier.height(64.dp)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(Icons.Default.Home, "Home")
            },
            label = {
                Text("Home", fontSize = 11.sp)
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(Icons.Default.ShoppingCart, "Home")
            },
            label = {
                Text("Cards", fontSize = 11.sp)
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(Icons.Default.Share, "Home")
            },
            label = {
                Text("Stats", fontSize = 11.sp)
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(Icons.Default.Home, "Home")
            },
            label = {
                Text("Profile", fontSize = 11.sp)
            }
        )
    }
}

@Composable
fun SpendingChart(
    values: List<Float> = listOf(30f, 52f, 40f, 84f, 48f, 24f, 36f),
    days: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    selected: Int = 3
) {
    val max = values.max()
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(800)) }

    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Canvas(Modifier.fillMaxWidth().height(76.dp)) {
            val barW = 20.dp.toPx()
            val gap = (size.width - barW * values.size) / (values.size - 1)
            values.forEachIndexed { i, v ->
                val h = size.height * (v / max) * progress.value
                drawRoundRect(
                    color = if (i == selected) Color(0xFF7F77DD) else Color(0xFFCECBF6),
                    topLeft = Offset(i * (barW + gap), size.height - h),
                    size = Size(barW, h),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEach {
                Text(
                    it, fontSize = 11.sp, color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingFivePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrainingFive()
    }
}