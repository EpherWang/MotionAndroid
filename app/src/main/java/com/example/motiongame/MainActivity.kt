package com.example.motiongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GameItem(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: String,
    val duration: String,
    val coverGradient: List<Color>,
)

data class ImuDevice(
    val id: String,
    val name: String,
    val abbreviation: String,
    val color: Color,
    val battery: Int,
    val eulerAngle: String,
    val status: ImuConnectionStatus,
)

enum class ImuConnectionStatus {
    Connected,
    Connecting,
    Disconnected,
}

private enum class AppScreen {
    GameList,
    ImuConnection,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionGameApp()
        }
    }
}

@Composable
fun MotionGameApp() {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.GameList) }

    MaterialTheme {
        Surface(color = AppColors.ScreenBackground, modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                AppScreen.GameList -> GameListScreen(
                    games = mockGameItems,
                    isDeviceConnected = false,
                    onSettingsClick = { },
                    onGameStart = { currentScreen = AppScreen.ImuConnection },
                )

                AppScreen.ImuConnection -> ImuConnectionScreen(
                    devices = mockImuDevices,
                    onBackClick = { currentScreen = AppScreen.GameList },
                    onHelpClick = { },
                    onStartCalibration = { },
                )
            }
        }
    }
}

@Composable
fun GameListScreen(
    games: List<GameItem>,
    isDeviceConnected: Boolean,
    onSettingsClick: () -> Unit,
    onGameStart: (String) -> Unit,
) {
    Scaffold(
        containerColor = AppColors.ScreenBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "体感游戏",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.Title,
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "设置",
                        tint = AppColors.Title,
                        modifier = Modifier.size(34.dp),
                    )
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                DeviceStatusCard(isConnected = isDeviceConnected)
            }
            items(games) { game ->
                GameCard(game = game, onStartClick = { onGameStart(game.id) })
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DeviceStatusCard(isConnected: Boolean) {
    val statusText = if (isConnected) "已连接" else "未连接"
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFDDE8FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Bluetooth, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("设备状态：")
                        withStyle(SpanStyle(color = AppColors.Primary, fontWeight = FontWeight.Bold)) { append(statusText) }
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Title,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("选择游戏后将引导连接 IMU", fontSize = 14.sp, color = AppColors.Body)
            }
        }
    }
}

@Composable
fun GameCard(game: GameItem, onStartClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 108.dp, height = 132.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(game.coverGradient)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = game.title.take(2), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(game.title, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Title)
                Spacer(modifier = Modifier.height(6.dp))
                Text(game.description, fontSize = 14.sp, color = AppColors.Body)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TagChip(text = "难度：${game.difficulty}", containerColor = difficultyChipColor(game.difficulty), textColor = difficultyTextColor(game.difficulty))
                    Spacer(modifier = Modifier.width(8.dp))
                    TagChip(text = game.duration, containerColor = Color(0xFFEAF0FF), textColor = AppColors.Primary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryActionButton(
                    text = "开始",
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(110.dp)
                        .height(48.dp),
                    onClick = onStartClick,
                )
            }
        }
    }
}

@Composable
fun ImuConnectionScreen(
    devices: List<ImuDevice>,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onStartCalibration: () -> Unit,
) {
    Scaffold(
        containerColor = AppColors.ScreenBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "返回", tint = AppColors.Title, modifier = Modifier.size(32.dp))
                }
                Text("连接 IMU", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Title)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onHelpClick) {
                    Icon(Icons.Outlined.HelpOutline, contentDescription = "帮助", tint = AppColors.Title, modifier = Modifier.size(32.dp))
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.ScreenBackground)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                PrimaryActionButton(
                    text = "开始校准",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    onClick = onStartCalibration,
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(devices) { ImuDeviceCard(it) }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun ImuDeviceCard(device: ImuDevice) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(device.color.copy(alpha = 0.75f), device.color))),
                contentAlignment = Alignment.Center,
            ) {
                Text(device.abbreviation, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(device.name, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = AppColors.Title)
                Spacer(modifier = Modifier.height(4.dp))
                Text("电量 ${device.battery}%", fontSize = 14.sp, color = AppColors.Body)
                Spacer(modifier = Modifier.height(2.dp))
                Text("欧拉角 ${device.eulerAngle}", fontSize = 14.sp, color = AppColors.Body)
            }
            StatusBadge(status = device.status)
        }
    }
}

@Composable
fun StatusBadge(status: ImuConnectionStatus) {
    val (text, color) = when (status) {
        ImuConnectionStatus.Connected -> "已连接" to Color(0xFF2DBE4D)
        ImuConnectionStatus.Connecting -> "连接中" to Color(0xFFFF9C1A)
        ImuConnectionStatus.Disconnected -> "未连接" to Color(0xFF9CA3AF)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(50)).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun TagChip(text: String, containerColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text = text, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PrimaryActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
    ) {
        Text(text = text, fontSize = 20.sp, textAlign = TextAlign.Center)
    }
}

private fun difficultyChipColor(difficulty: String): Color = when (difficulty) {
    "高" -> Color(0xFFFFEEE8)
    "中等" -> Color(0xFFFFF2E5)
    else -> Color(0xFFEAF8EA)
}

private fun difficultyTextColor(difficulty: String): Color = when (difficulty) {
    "高" -> Color(0xFFD65535)
    "中等" -> Color(0xFFDA6C1E)
    else -> Color(0xFF4B9A45)
}

private object AppColors {
    val ScreenBackground = Color(0xFFF7F9FC)
    val Primary = Color(0xFF2D6BFF)
    val Title = Color(0xFF101828)
    val Body = Color(0xFF667085)
}

private val mockGameItems = listOf(
    GameItem("boxing", "节奏拳击", "跟随节奏完成挥拳动作", "中等", "3分钟", listOf(Color(0xFF243B8F), Color(0xFF7D1E6A))),
    GameItem("dodge", "敏捷闪避", "快速躲避来袭目标", "高", "2分钟", listOf(Color(0xFF13386F), Color(0xFF1F7FD9))),
    GameItem("balance", "平衡挑战", "保持稳定姿态完成关卡", "低", "5分钟", listOf(Color(0xFF8DC7D6), Color(0xFF4A8DA0))),
    GameItem("reaction", "反应训练", "根据提示做出指定动作", "中等", "4分钟", listOf(Color(0xFF322E8E), Color(0xFF6259E1))),
)

private val mockImuDevices = listOf(
    ImuDevice("head", "头", "H", Color(0xFF2D6BFF), 86, "12° / -6° / 35°", ImuConnectionStatus.Connected),
    ImuDevice("waist", "腰", "W", Color(0xFFFFA726), 78, "-8° / 5° / 20°", ImuConnectionStatus.Connected),
    ImuDevice("left_arm", "左小臂", "LA", Color(0xFF46C75B), 92, "15° / 10° / -18°", ImuConnectionStatus.Connected),
    ImuDevice("right_arm", "右小臂", "RA", Color(0xFF8B5CF6), 67, "-12° / 8° / 30°", ImuConnectionStatus.Connecting),
    ImuDevice("left_leg", "左小腿", "LL", Color(0xFFEF5350), 81, "5° / -3° / 12°", ImuConnectionStatus.Connected),
    ImuDevice("right_leg", "右小腿", "RL", Color(0xFF27C7D7), 43, "-20° / 4° / -15°", ImuConnectionStatus.Disconnected),
)

@Preview(showBackground = true)
@Composable
fun GameListPreview() {
    GameListScreen(games = mockGameItems, isDeviceConnected = false, onSettingsClick = {}, onGameStart = {})
}

@Preview(showBackground = true)
@Composable
fun ImuConnectionPreview() {
    ImuConnectionScreen(devices = mockImuDevices, onBackClick = {}, onHelpClick = {}, onStartCalibration = {})
}
