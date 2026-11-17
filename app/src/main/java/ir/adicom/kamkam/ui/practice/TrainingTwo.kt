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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrainingTwo() {

    Column(
        modifier = Modifier
            .width(280.dp)
            .border(
                1.dp,
                Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier.height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Settings", fontSize = 22.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        ItemRow(
            icon = Icons.Default.Notifications,
            bgColor = PurpleColor,
            title = "Notifications",
            subTitle = "Push and email alerts",
            trailing = {
                Switch(
                    checked = true,
                    onCheckedChange = { },
                )
            }
        )
        HorizontalDivider(
            Modifier.padding(start = 72.dp),
            color = Color.LightGray
        )
        ItemRow(
            icon = Icons.Default.Place,
            bgColor = TealColor,
            title = "Language",
            subTitle = "English",
            trailing = {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = ""
                )
            }
        )
        HorizontalDivider(
            Modifier.padding(start = 72.dp),
            color = Color.LightGray
        )
        ItemRow(
            icon = Icons.Default.ShoppingCart,
            bgColor = AmberColor,
            title = "Dark mode",
            subTitle = "Follow system",
            trailing = {
                Switch(
                    checked = false,
                    onCheckedChange = { },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Red,
                        checkedTrackColor = Color.Yellow
                    )
                )
            }
        )

    }
}

val PurpleColor = Color(0xFF773EFF)
val TealColor = Color(0xFF008080)
val AmberColor = Color(0xFFFFBF00)

@Composable
fun ItemRow(
    icon: ImageVector,
    bgColor: Color,
    title: String,
    subTitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(bgColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = icon.name,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column() {
            Text(title, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(subTitle, fontSize = 13.sp, color = Color.Gray)
        }
        Spacer(Modifier.weight(1f))
        trailing()
        Spacer(Modifier.width(16.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingTwoPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrainingTwo()
    }
}