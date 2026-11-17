package ir.adicom.kamkam.ui.practice

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrainingThree() {

    Column(
        modifier = Modifier
            .width(240.dp)
            .border(
                1.dp,
                Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        Box(
            Modifier
                .height(160.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE6F1FB),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .border(1.dp, Color(0xffFF7F50), RoundedCornerShape(12.dp))
                    .background(Color(0x4fFF7F50), RoundedCornerShape(12.dp))
                    .size(44.dp, 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("-20%", color = Color(0xffFF7F50), fontSize = 12.sp, maxLines = 1)
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.White, CircleShape)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FavoriteBorder, "")
            }
            Text("Image Placeholder", fontSize = 14.sp)
        }
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text("Wireless Headphones", fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Star, "", tint = AmberColor, modifier = Modifier.size(16.dp))
                Text("4.8 (120)", color = Color.Gray, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("$89", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$112", color = Color.Gray, fontSize = 14.sp, textDecoration = TextDecoration.LineThrough)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE6F1FB),
                    contentColor = Color(0xFF0C447C)
                ),
                border = BorderStroke(1.dp, Color(0xff80A8D1))
            ) {
                Text("Add to cart", fontSize = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingThreePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TrainingThree()
    }
}