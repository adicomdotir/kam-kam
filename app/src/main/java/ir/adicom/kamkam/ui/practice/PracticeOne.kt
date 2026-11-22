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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PracticeOne() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Welcome back", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Log in to continue", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text("Email")
            },
            leadingIcon = {
                Icon(Icons.Default.Email, "")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text("Password")
            },
            leadingIcon = {
                Icon(Icons.Default.Lock, "")
            },
            trailingIcon = {
                Icon(Icons.Default.Warning, "")
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Forgot password?", fontSize = 13.sp, color = Color.Blue)

        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .height(52.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE6F1FB),
                    shape = RoundedCornerShape(size = 12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xff80A8D1),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center

        ) {
            Column {
                Text("Log in", color = Color(0xFF0C447C), fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(Modifier.weight(1.0f))
            Text("Or", fontSize = 13.sp)
            HorizontalDivider(Modifier.weight(1.0f))
        }

        Text("Don't have an account? Sign up", fontSize = 13.sp, color = Color.Blue)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PracticeOnePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PracticeOne()
    }
}