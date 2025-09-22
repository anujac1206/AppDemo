@file:Suppress("UnusedImport")

package com.example.offlinefirstapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlin.math.min
import kotlin.random.Random

enum class UserRole { Student, Teacher }

@Composable
fun OfflineFirstApp(
    role: UserRole,
    onChangeRole: (UserRole) -> Unit,
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (role == UserRole.Student) "Student" else "Teacher")
                },
                actions = {
                    RoleSwitcher(role = role, onChangeRole = onChangeRole)
                    IconButton(onClick = { /* open profile/settings */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            if (role == UserRole.Student) {
                StudentBottomBar(navController)
            }
        }
    ) { inner ->
        Surface(Modifier.padding(inner)) {
            if (role == UserRole.Student) {
                StudentNavHost(navController)
            } else {
                TeacherTabs()
            }
        }
    }
}

@Composable
private fun RoleSwitcher(role: UserRole, onChangeRole: (UserRole) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(if (role == UserRole.Student) "Student" else "Teacher") },
            leadingIcon = {
                Icon(
                    imageVector = if (role == UserRole.Student) Icons.Default.MenuBook else Icons.Default.Upload,
                    contentDescription = null
                )
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Student") },
                onClick = { expanded = false; onChangeRole(UserRole.Student) }
            )
            DropdownMenuItem(
                text = { Text("Teacher") },
                onClick = { expanded = false; onChangeRole(UserRole.Teacher) }
            )
        }
    }
}

/* -------------------------- Student Side -------------------------- */

private enum class StudentRoute(val route: String, val label: String) {
    Home("home", "Home"),
    Live("live", "Live"),
    Capsules("capsules", "Capsules"),
    Quizzes("quizzes", "Quizzes"),
    Resources("resources", "Resources"),
    Wallet("wallet", "Wallet")
}

@Composable
private fun StudentBottomBar(navController: NavHostController) {
    val items = listOf(
        StudentRoute.Home to Icons.Default.Home,
        StudentRoute.Live to Icons.Default.LiveTv,
        StudentRoute.Capsules to Icons.Default.LibraryBooks,
        StudentRoute.Quizzes to Icons.Default.Quiz,
        StudentRoute.Resources to Icons.Default.MoreHoriz,
        StudentRoute.Wallet to Icons.Default.DataUsage
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val current = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { (screen, icon) ->
            NavigationBarItem(
                selected = current == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(badge = {
                        if (screen == StudentRoute.Live) Badge { Text("1") }
                    }) {
                        Icon(icon, contentDescription = screen.label)
                    }
                },
                label = { Text(screen.label, maxLines = 1) }
            )
        }
    }
}

@Composable
private fun StudentNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = StudentRoute.Home.route) {
        composable(StudentRoute.Home.route) { StudentHomeScreen() }
        composable(StudentRoute.Live.route) { LiveClassScreen() }
        composable(StudentRoute.Capsules.route) { LectureCapsulesScreen() }
        composable(StudentRoute.Quizzes.route) { QuizzesScreen() }
        composable(StudentRoute.Resources.route) { ResourcesScreen() }
        composable(StudentRoute.Wallet.route) { DataWalletScreen() }
    }
}

@Composable
private fun StudentHomeScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(onClick = { /* open live */ }) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text("Upcoming Live Class", style = MaterialTheme.typography.titleMedium)
                    Text("Maths - 10:30 AM", color = Color.Gray)
                }
                AssistChip(onClick = { }, label = { Text("Notify Me") })
            }
        }
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Cached Capsules", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    (1..3).forEach {
                        CapsuleChip("Capsule $it", downloaded = true, expiresInDays = 7 - it)
                    }
                }
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recent Quizzes", style = MaterialTheme.typography.titleMedium)
                QuizRow(title = "Algebra Quick 5", score = "3/5", offline = true)
                QuizRow(title = "History Pop", score = "5/5", offline = false)
            }
        }
        DataWalletMini(expectedMb = 120, actualMb = 96)
    }
}

@Composable
private fun CapsuleChip(title: String, downloaded: Boolean, expiresInDays: Int) {
    AssistChip(
        onClick = { },
        label = { Text("$title (${expiresInDays}d)") },
        leadingIcon = {
            Icon(
                if (downloaded) Icons.Default.CloudOff else Icons.Default.CloudQueue,
                contentDescription = null
            )
        }
    )
}

@Composable
private fun LiveClassScreen() {
    var showQuiz by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Live Class", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        SlideViewerPlaceholder()
        TranscriptBox(lines = listOf("Teacher: Welcome everyone...", "Student: Yes!"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showQuiz = true }) { Text("Launch Quiz (demo)") }
            OutlinedButton(onClick = { /* reactions */ }) { Text("👍 👏 ❤️") }
        }
    }
    if (showQuiz) {
        SimpleQuizDialog(onDismiss = { }, onSubmit = { showQuiz = false })
    }
}

@Composable
private fun SlideViewerPlaceholder() {
    Card(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Slides (compressed PNGs)")
        }
    }
}

@Composable
private fun TranscriptBox(lines: List<String>) {
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Real-time Transcript", style = MaterialTheme.typography.titleMedium)
            lines.takeLast(4).forEach { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
    }
}

@Composable
private fun LectureCapsulesScreen() {
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.horizontalScroll(scroll), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(8) { idx ->
                LectureCapsuleCard(
                    title = "Capsule ${idx + 1}",
                    mins = 15,
                    downloaded = idx % 2 == 0,
                    onDownload = { /* schedule night download */ },
                    onDelete = { /* auto-delete in 7 days */ }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Auto-download at night (simulated). Auto-delete after 7 days.", color = Color.Gray)
    }
}

@Composable
private fun LectureCapsuleCard(
    title: String,
    mins: Int,
    downloaded: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.width(220.dp).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text("$mins min", color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (downloaded) "Re-download" else "Download")
                }
                AssistChip(onClick = onDelete, label = { Text("Auto-delete 7d") })
            }
        }
    }
}

@Composable
private fun QuizzesScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Quizzes (JSON-based)", style = MaterialTheme.typography.titleMedium)
        QuizRow("Geography 5", "—", offline = true)
        QuizRow("Physics Pop", "—", offline = true)
        Divider()
        Text("IVR Simulation", style = MaterialTheme.typography.titleMedium)
        IVRKeypad(onKey = { /* enqueue DTMF */ })
    }
}

@Composable
private fun QuizRow(title: String, score: String, offline: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(if (offline) "Offline mode enabled" else "Online required", color = Color.Gray)
        }
        AssistChip(onClick = { /* start */ }, label = { Text(score.ifBlank { "Start" }) })
    }
}

@Composable
private fun IVRKeypad(onKey: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val keys = listOf(1,2,3,4,5,6,7,8,9,0)
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                row.forEach { k ->
                    Button(onClick = { onKey(k) }, modifier = Modifier.width(80.dp)) {
                        Text(k.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourcesScreen() {
    var autoDelete by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Resources (compressed PDFs/PPTs)", style = MaterialTheme.typography.titleMedium)
        ResourceRow("Week1_Notes.pdf", sizeKb = 420, cached = true)
        ResourceRow("Algebra_Slides.pptx", sizeKb = 980, cached = false)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = autoDelete, onCheckedChange = { autoDelete = it })
            Spacer(Modifier.width(8.dp))
            Text("Auto-delete cached resources")
        }
    }
}

@Composable
private fun ResourceRow(name: String, sizeKb: Int, cached: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(name, fontWeight = FontWeight.Medium)
            Text("${sizeKb}KB • ${if (cached) "Cached offline" else "Tap to cache"}", color = Color.Gray)
        }
        OutlinedButton(onClick = { /* toggle cache */ }) {
            Text(if (cached) "Remove" else "Cache")
        }
    }
}

@Composable
private fun DataWalletMini(expectedMb: Int, actualMb: Int) {
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Data Wallet", style = MaterialTheme.typography.titleMedium)
            DataWalletGraph(expectedMb = expectedMb, actualMb = actualMb, height = 72.dp)
            Text("Today: expected ${expectedMb}MB vs used ${actualMb}MB", color = Color.Gray)
        }
    }
}

@Composable
private fun DataWalletScreen() {
    var day by remember { mutableIntStateOf(0) }
    val expected = listOf(120, 90, 150, 110, 80, 130, 95)
    val actual = List(7) { (expected[it] * (0.7f + Random.nextFloat() * 0.5f)).toInt() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Data Wallet (USP)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        DataWalletGraph(expectedMb = expected[day], actualMb = actual[day], height = 160.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            expected.indices.forEach {
                AssistChip(onClick = { day = it }, label = { Text("Day ${it + 1}") })
            }
        }
        Card {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Per lecture stats")
                Text("Capsule A: 12MB • Capsule B: 8MB • Live: 35MB", color = Color.Gray)
                Text("Weekly summary generated", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun DataWalletGraph(expectedMb: Int, actualMb: Int, height: Dp) {
    Card {
        Box(Modifier.fillMaxWidth().height(height).padding(12.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val maxMb = maxOf(expectedMb, actualMb).coerceAtLeast(1)
                fun toHeight(mb: Int) = h * (mb / maxMb.toFloat())
                // Expected (gray)
                drawLine(
                    color = Color.LightGray,
                    start = androidx.compose.ui.geometry.Offset(w * 0.25f, h),
                    end = androidx.compose.ui.geometry.Offset(w * 0.25f, h - toHeight(expectedMb)),
                    strokeWidth = 40f,
                    cap = StrokeCap.Round
                )
                // Actual (primary)
                drawLine(
                    color = MaterialTheme.colorScheme.primary,
                    start = androidx.compose.ui.geometry.Offset(w * 0.75f, h),
                    end = androidx.compose.ui.geometry.Offset(w * 0.75f, h - toHeight(actualMb)),
                    strokeWidth = 40f,
                    cap = StrokeCap.Round
                )
            }
            Row(Modifier.align(Alignment.TopStart), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Expected ${expectedMb}MB") })
                AssistChip(onClick = {}, label = { Text("Actual ${actualMb}MB") })
            }
        }
    }
}

@Composable
private fun SimpleQuizDialog(onDismiss: () -> Unit, onSubmit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Quiz") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("What is 2 + 2?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1", "2", "3", "4").forEach { ans ->
                        AssistChip(onClick = { /* select */ }, label = { Text(ans) })
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onSubmit) { Text("Submit") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Later") } }
    )
}

/* -------------------------- Teacher Side -------------------------- */

@Composable
private fun TeacherTabs() {
    var tab by remember { mutableIntStateOf(0) }
    val titles = listOf("Upload", "Schedule", "Live", "Analytics")
    Column {
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index },
                    text = { Text(title) },
                    icon = {
                        val icon = when (index) {
                            0 -> Icons.Default.Upload
                            1 -> Icons.Default.LiveTv
                            2 -> Icons.Default.LiveTv
                            else -> Icons.Default.Analytics
                        }
                        Icon(icon, contentDescription = null)
                    }
                )
            }
        }
        when (tab) {
            0 -> TeacherUploadScreen()
            1 -> TeacherScheduleScreen()
            2 -> TeacherLiveSessionScreen()
            3 -> TeacherAnalyticsScreen()
        }
    }
}

@Composable
private fun TeacherUploadScreen() {
    var title by remember { mutableStateOf("") }
    var compressSlides by remember { mutableStateOf(true) }
    var compressAudio by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Upload PPT/Audio → Auto-compress", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = compressSlides, onCheckedChange = { compressSlides = it })
            Spacer(Modifier.width(8.dp)); Text("Compress slides (<50KB each)")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = compressAudio, onCheckedChange = { compressAudio = it })
            Spacer(Modifier.width(8.dp)); Text("Compress audio (Opus)")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { /* pick ppt/audio */ }) { Text("Pick Files") }
            OutlinedButton(onClick = { /* upload */ }) { Text("Upload") }
        }
    }
}

@Composable
private fun TeacherScheduleScreen() {
    var name by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("10:30") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Schedule Class → Push to students", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Class title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { /* schedule */ }) { Text("Schedule") }
            OutlinedButton(onClick = { /* notify */ }) { Text("Notify") }
        }
    }
}

@Composable
private fun TeacherLiveSessionScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Live Session Controller", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { /* start */ }) { Text("Start Audio + Slide Sync") }
            OutlinedButton(onClick = { /* end */ }) { Text("End") }
        }
        Divider()
        Text("Quick Quiz", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { /* 1 */ }, label = { Text("A") })
            AssistChip(onClick = { /* 2 */ }, label = { Text("B") })
            AssistChip(onClick = { /* 3 */ }, label = { Text("C") })
            AssistChip(onClick = { /* 4 */ }, label = { Text("D") })
        }
    }
}

@Composable
private fun TeacherAnalyticsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Card {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Attendance: 82%")
                Text("Engagement: 67% (reactions, quiz responses)")
                Text("Data usage per student: median 95MB")
            }
        }
    }
}

/* -------------------------- App Entrypoint Preview -------------------------- */

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun PreviewStudent() {
    MaterialTheme {
        OfflineFirstApp(role = UserRole.Student, onChangeRole = {})
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun PreviewTeacher() {
    MaterialTheme {
        OfflineFirstApp(role = UserRole.Teacher, onChangeRole = {})
    }
}