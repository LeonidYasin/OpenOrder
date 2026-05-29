package com.example.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*

// Custom Color Palette for the Cosmic P2P Brotherhood
val SpaceDark = Color(0xFF0F1219)
val CosmicGray = Color(0xFF1B202D)
val AccentTeal = Color(0xFF00E5FF)
val HighContrastGold = Color(0xFFFFBF00)
val CyberGreen = Color(0xFF00E676)
val SoftCyan = Color(0xFF80DEEA)
val TextLight = Color(0xFFE3E9F3)
val TextMuted = Color(0xFF8C96A6)
val AlertRed = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenOrderSocialOSApp(viewModel: SuiteViewModel) {
    val currentAgentId by viewModel.currentAgentId.collectAsState()
    val selectedForkId by viewModel.selectedForkId.collectAsState()
    val agents by viewModel.agents.collectAsState()
    val proposals by viewModel.proposals.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val disputes by viewModel.disputes.collectAsState()
    val forks by viewModel.forks.collectAsState()
    val syncLogs by viewModel.syncLogs.collectAsState()
    val consoleOutput by viewModel.consoleOutput.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Идеология, 1: Совет & Управа, 2: Суд, 3: Форки & Сеть
    var showRegisterDialog by remember { mutableStateOf(false) }

    val currentAgent = agents.find { it.id == currentAgentId }
    val currentFork = forks.find { it.id == selectedForkId }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceDark,
                    titleContentColor = AccentTeal
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Order Logo",
                            tint = AccentTeal,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "ОТКРЫТЫЙ ОРДЕН",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Text(
                                "Social Operating System • P2P Kernel v1.0",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.triggerSync() },
                        modifier = Modifier.testTag("sync_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync P2P Mail",
                            tint = AccentTeal
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SpaceDark,
                contentColor = TextLight,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Home, "Architecture") },
                    label = { Text("Идеология", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpaceDark,
                        selectedTextColor = AccentTeal,
                        indicatorColor = AccentTeal,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.CheckCircle, "Council") },
                    label = { Text("Совет & Управа", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpaceDark,
                        selectedTextColor = AccentTeal,
                        indicatorColor = AccentTeal,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Warning, "Judiciary") },
                    label = { Text("Суд", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpaceDark,
                        selectedTextColor = AccentTeal,
                        indicatorColor = AccentTeal,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Email, "Repository") },
                    label = { Text("Форки & Сеть", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SpaceDark,
                        selectedTextColor = AccentTeal,
                        indicatorColor = AccentTeal,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceDark)
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            // Welcome & Current Perspective Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Действующий Агент (Сессия):",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Репутация: ",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                            Text(
                                text = "${currentAgent?.reputationScore ?: 0.0} REP",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = HighContrastGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        Box {
                            Text(
                                text = "${currentAgent?.name ?: "Неизвестен"} • [${currentAgent?.role ?: ""}] ▾",
                                color = AccentTeal,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { expanded = true }
                                    .padding(vertical = 4.dp)
                                    .testTag("agent_selector")
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(CosmicGray)
                            ) {
                                agents.forEach { agent ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${agent.name} (${agent.reputationScore} REP - ${agent.role})",
                                                color = if (agent.id == currentAgentId) AccentTeal else TextLight
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectAgent(agent.id)
                                            expanded = false
                                        }
                                    )
                                }
                                HorizontalDivider(color = TextMuted.copy(alpha = 0.3f))
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Add,
                                                "Add icon",
                                                tint = HighContrastGold,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Вступить/Регистрация", color = HighContrastGold, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    onClick = {
                                        showRegisterDialog = true
                                        expanded = false
                                    }
                                )
                            }
                        }

                        // Selected Fork indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Share,
                                "Fork Icon",
                                tint = SoftCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Форк: ${currentFork?.title?.replace(" (v1.0.0)", "") ?: "Ядро"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SoftCyan)
                            )
                        }
                    }
                }
            }

            // Real-Time System Command Console
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(bottom = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, AccentTeal.copy(0.3f))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    reverseLayout = true
                ) {
                    item {
                        Text(
                            text = consoleOutput,
                            color = CyberGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> ArchitectureTab(viewModel) { activeTab = it }
                    1 -> CouncilAndTasksTab(viewModel, proposals, tasks, currentAgentId, selectedForkId)
                    2 -> JudiciaryTab(viewModel, disputes, agents, currentAgentId)
                    3 -> RepositoryForksTab(viewModel, forks, syncLogs, selectedForkId, agents)
                }
            }

            if (showRegisterDialog) {
                var regId by remember { mutableStateOf("") }
                var regName by remember { mutableStateOf("") }
                var regRole by remember { mutableStateOf("Knight") }
                val roles = listOf("Adept", "Knight", "Arbiter", "Scribe", "Grandmaster")
                var regRoleExpanded by remember { mutableStateOf(false) }
                var regInitialRep by remember { mutableStateOf(20f) }
                var regAutoLogin by remember { mutableStateOf(true) }
                var regCustomPubKey by remember { mutableStateOf("") }

                LaunchedEffect(showRegisterDialog) {
                    if (showRegisterDialog) {
                        regId = ""
                        regName = ""
                        regCustomPubKey = "0x" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
                        regInitialRep = 20f
                    }
                }

                AlertDialog(
                    onDismissRequest = { showRegisterDialog = false },
                    title = {
                        Column {
                            Text(
                                text = "ВСТУПИТЬ В ОТКРЫТЫЙ ОРДЕН",
                                color = HighContrastGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Создание децентрализованного манифеста и ключей доступа",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = regId,
                                onValueChange = { regId = it },
                                label = { Text("P2P ID (например, user@orden.p2p)") },
                                placeholder = { Text("id@orden.p2p") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentTeal,
                                    focusedLabelColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_id_input"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it },
                                label = { Text("Имя / Псевдоним") },
                                placeholder = { Text("Антон") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentTeal,
                                    focusedLabelColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                                singleLine = true
                            )

                            // Role selector Dropdown
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = when (regRole) {
                                        "Grandmaster" -> "Грандмастер (Grandmaster)"
                                        "Knight" -> "Рыцарь (Knight)"
                                        "Arbiter" -> "Арбитр (Arbiter)"
                                        "Scribe" -> "Писец (Scribe)"
                                        else -> "Адепт (Adept)"
                                    },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Выбор Орденской Роли") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentTeal,
                                        focusedLabelColor = AccentTeal,
                                        unfocusedBorderColor = TextMuted,
                                        unfocusedLabelColor = TextMuted,
                                        focusedTextColor = TextLight,
                                        unfocusedTextColor = TextLight
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { regRoleExpanded = true },
                                    trailingIcon = {
                                        IconButton(onClick = { regRoleExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, "Select Role", tint = AccentTeal)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = regRoleExpanded,
                                    onDismissRequest = { regRoleExpanded = false },
                                    modifier = Modifier.background(CosmicGray).fillMaxWidth(0.8f)
                                ) {
                                    roles.forEach { r ->
                                        val rName = when (r) {
                                            "Grandmaster" -> "Грандмастер"
                                            "Knight" -> "Рыцарь"
                                            "Arbiter" -> "Арбитр"
                                            "Scribe" -> "Писец"
                                            else -> "Адепт"
                                        }
                                        DropdownMenuItem(
                                            text = { Text("$rName ($r)", color = TextLight) },
                                            onClick = {
                                                regRole = r
                                                regRoleExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Pub key display
                            OutlinedTextField(
                                value = regCustomPubKey,
                                onValueChange = { regCustomPubKey = it },
                                label = { Text("Ed25519 Публичный ключ (генерируется)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentTeal,
                                    focusedLabelColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextMuted,
                                    unfocusedTextColor = TextMuted
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        regCustomPubKey = "0x" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
                                    }) {
                                        Icon(Icons.Default.Refresh, "Re-generate key", tint = AccentTeal)
                                    }
                                }
                            )

                            // Initial Reputation
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Стартовая репутация:", color = TextLight, fontSize = 12.sp)
                                    Text("${regInitialRep.toInt()} REP", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Slider(
                                    value = regInitialRep,
                                    onValueChange = { regInitialRep = it },
                                    valueRange = 10f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentTeal,
                                        activeTrackColor = AccentTeal,
                                        inactiveTrackColor = TextMuted.copy(alpha = 0.3f)
                                    )
                                )
                            }

                            // Auto login checkbox
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { regAutoLogin = !regAutoLogin },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = regAutoLogin,
                                    onCheckedChange = { regAutoLogin = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AccentTeal,
                                        uncheckedColor = TextMuted
                                    )
                                )
                                Text("Автоматически войти под этой сессией", color = TextLight, fontSize = 13.sp)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (regName.isBlank() || regId.isBlank()) return@Button
                                val finalId = if (regId.contains("@")) regId else "${regId.trim()}@orden.p2p"
                                viewModel.registerAgent(
                                    id = finalId,
                                    name = regName.trim(),
                                    role = regRole,
                                    initialRep = regInitialRep.toDouble(),
                                    customPubKey = regCustomPubKey,
                                    autoLogin = regAutoLogin
                                )
                                showRegisterDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark)
                        ) {
                            Text("ВСТУПИТЬ В ОРДЕН", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRegisterDialog = false }) {
                            Text("ОТМЕНА", color = TextMuted)
                        }
                    },
                    containerColor = CosmicGray,
                    tonalElevation = 8.dp
                )
            }

            if (showOnboarding) {
                OnboardingWelcomeDialog(
                    onDismiss = { viewModel.dismissOnboarding() },
                    onQuickJoin = {
                        val names = listOf("Радомир", "Святополк", "Данила", "Ярослав", "Всеволод", "Мирослава", "Лада", "Ольга", "Добрыня", "Любомир")
                        val rId = "order_recruit_${(10..99).random()}@orden.p2p"
                        val rName = "${names.random()} (Рекрут)"
                        viewModel.registerAgent(rId, rName, "Knight")
                        viewModel.dismissOnboarding()
                        activeTab = 0
                    },
                    onStartQuest = {
                        viewModel.dismissOnboarding()
                        activeTab = 0
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingWelcomeDialog(
    onDismiss: () -> Unit,
    onQuickJoin: () -> Unit,
    onStartQuest: () -> Unit
) {
    var step by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentTeal,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "🌌 ОТКРЫТЫЙ ОРДЕН",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HighContrastGold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stepper indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (index <= step) AccentTeal else CosmicGray)
                        )
                    }
                }

                when (step) {
                    0 -> {
                        Text(
                            text = "Приветствуем в Social OS!",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Вы открыли интерфейс децентрализованной цифровой республики со встроенным суверенитетом и властью репутации.\n\nЗдесь нет паролей, центральных баз данных или модераторов: все решения и данные синхронизируются пир-ту-пир прямо поверх электронной почты!",
                            color = TextLight,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    1 -> {
                        Text(
                            text = "4 Ключевых Цифровых Института",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🏛️ Законодательный Совет\nГолосование репутацией за реформы ордена и форки кода.\n\n🔧 Исполнительное Братство\nБрать на себя и закрывать задачи, зарабатывая очки REP.\n\n⚖️ Независимый Суд\nКонституционные споры присяжных для соблюдения законов.\n\n📡 Стек Сетевой Синхронизации\nP2P транспорт через SMTP/IMAP поверх любых почтовых узлов.",
                            color = TextLight,
                            fontSize = 13.sp,
                            lineHeight = 16.sp
                        )
                    }
                    2 -> {
                        Text(
                            text = "Интерактивный Квест Сообщества",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Пройдите 4 простых шага, чтобы разобраться:\n1️⃣ Принять присягу (зарегистрировать ID)\n2️⃣ Сделать первый выбор в Совете\n3️⃣ Ознакомиться со спором в Суде\n4️⃣ Запустить мониторинг и синхрон по P2P\n\nГотовы вступить в братство и получить первые баллы репутации?",
                            color = TextLight,
                            fontSize = 13.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step < 2) {
                    Button(
                        onClick = { step++ },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Далее ➡️", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onQuickJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SpaceDark),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("onboarding_quick_join_btn")
                    ) {
                        Text("⚔️ Вступить!", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onStartQuest,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("onboarding_start_quest_btn")
                    ) {
                        Text("🎯 Квесты", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        },
        dismissButton = {
            if (step > 0) {
                TextButton(onClick = { step-- }) {
                    Text("⬅️ Назад", color = TextMuted, fontSize = 11.sp)
                }
            } else {
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.testTag("onboarding_skip_btn")
                ) {
                    Text("Пропустить", color = TextMuted, fontSize = 11.sp)
                }
            }
        },
        containerColor = CosmicGray,
        tonalElevation = 8.dp
    )
}

// -------------------------------------------------------------
// TAB 0: DESCRIPTION & OOP CLASS MODEL MODEL
// -------------------------------------------------------------
@Composable
fun ArchitectureTab(
    viewModel: SuiteViewModel,
    onSwitchTab: (Int) -> Unit
) {
    var subTab by remember { mutableStateOf(0) } // 0: Быстрый Старт, 1: Институты, 2: Классовая Модель, 3: P2P Стек

    val mRegistered by viewModel.missionRegistered.collectAsState()
    val mVoted by viewModel.missionVoted.collectAsState()
    val mDispute by viewModel.missionDispute.collectAsState()
    val mSynced by viewModel.missionSynced.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { subTab = 0 },
                modifier = Modifier.weight(1.1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTab == 0) AccentTeal else CosmicGray,
                    contentColor = if (subTab == 0) SpaceDark else TextLight
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("🚀 Старт", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { subTab = 1 },
                modifier = Modifier.weight(1.1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTab == 1) AccentTeal else CosmicGray,
                    contentColor = if (subTab == 1) SpaceDark else TextLight
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("🏛️ Институты", fontSize = 10.sp)
            }
            Button(
                onClick = { subTab = 2 },
                modifier = Modifier.weight(1.1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTab == 2) AccentTeal else CosmicGray,
                    contentColor = if (subTab == 2) SpaceDark else TextLight
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("💻 Классы", fontSize = 10.sp)
            }
            Button(
                onClick = { subTab = 3 },
                modifier = Modifier.weight(1.1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (subTab == 3) AccentTeal else CosmicGray,
                    contentColor = if (subTab == 3) SpaceDark else TextLight
                ),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("📡 P2P Стек", fontSize = 10.sp)
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            when (subTab) {
                0 -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray),
                            border = BorderStroke(1.dp, AccentTeal.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "🌌 ДОБРО ПОЖАЛОВАТЬ В ОТКРЫТЫЙ ОРДЕН!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HighContrastGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Вы зашли в Social OS — децентрализованную цифровую республику со встроенным репутационным суверенитетом. Здесь нет ни одного централизованного сервера: все решения синхронизируются пир-ту-пир прямо поверх электронной почты.",
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Пройдите увлекательный Интерактивный Квест Сообщества, чтобы понять, как устроена цифровая власть, заслужить первые очки репутации (REP) и влиться в братство!",
                                    color = SoftCyan,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { viewModel.resetOnboarding() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp).testTag("reopen_onboarding_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = AccentTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Повторить презентацию", color = AccentTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Progress Tracker Bar
                    item {
                        val completedCount = (if (mRegistered) 1 else 0) + 
                                             (if (mVoted) 1 else 0) + 
                                             (if (mDispute) 1 else 0) + 
                                             (if (mSynced) 1 else 0)
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black),
                            border = BorderStroke(1.dp, CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "🏆 Прогресс Квеста Новичка:",
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "$completedCount из 4 закладок",
                                        color = if (completedCount == 4) CyberGreen else HighContrastGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { completedCount / 4.0f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = if (completedCount == 4) CyberGreen else AccentTeal,
                                    trackColor = CosmicGray
                                )
                            }
                        }
                    }

                    // Mission 1 Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            tint = if (mRegistered) CyberGreen else HighContrastGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Шаг 1: Принять присягу (Создать ID)",
                                            fontWeight = FontWeight.Bold,
                                            color = AccentTeal,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (mRegistered) {
                                        Text("🏆 ВЫПОЛНЕНО", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission1_done"))
                                    } else {
                                        Text("⏳ ОЖИДАЕТ", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission1_pending"))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Создайте собственный суверенный P2P крипто-идентификатор в формате user@orden.p2p и подпишите общественный договор.",
                                    color = TextLight,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val names = listOf("Радомир", "Святополк", "Данила", "Ярослав", "Всеволод", "Мирослава", "Лада", "Ольга", "Добрыня", "Любомир")
                                            val rId = "order_recruit_${(10..99).random()}@orden.p2p"
                                            val rName = "${names.random()} (Рекрут)"
                                            viewModel.registerAgent(rId, rName, "Knight")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                                        modifier = Modifier.weight(1f).height(36.dp).testTag("quick_join_btn"),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("⚔️ Вступить в 1 клик", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            onSwitchTab(3) // Switch to tab 3 to configure
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                                        border = BorderStroke(1.dp, TextMuted),
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("⚙️ Полная настройка", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Mission 2 Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (mVoted) CyberGreen else HighContrastGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Шаг 2: Первый голос в Совете",
                                            fontWeight = FontWeight.Bold,
                                            color = AccentTeal,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (mVoted) {
                                        Text("🏆 ВЫПОЛНЕНО", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission2_done"))
                                    } else {
                                        Text("⏳ ОЖИДАЕТ", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission2_pending"))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Влияйте на законодательство Ордена! Ознакомьтесь с 'Принятием Хартии Цифрового Суверенитета' и отдайте свой голос за или против.",
                                    color = TextLight,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onSwitchTab(1) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (mVoted) CosmicGray else HighContrastGold, contentColor = SpaceDark),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("go_to_council_btn"),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("🗳️ Перейти к Голосованию", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (mVoted) TextLight else SpaceDark)
                                }
                            }
                        }
                    }

                    // Mission 3 Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (mDispute) CyberGreen else HighContrastGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Шаг 3: Судейское правосудие",
                                            fontWeight = FontWeight.Bold,
                                            color = AccentTeal,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (mDispute) {
                                        Text("🏆 ВЫПОЛНЕНО", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission3_done"))
                                    } else {
                                        Text("⏳ ОЖИДАЕТ", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission3_pending"))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "В Ордене нет полиции — порядок поддерживают сами участники. Примите участие в рассмотрении конституционного спора dispute-001 в качестве присяжного.",
                                    color = TextLight,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onSwitchTab(2) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (mDispute) CosmicGray else HighContrastGold, contentColor = SpaceDark),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("go_to_court_btn"),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("⚖️ Открыть Судебные Тяжбы", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (mDispute) TextLight else SpaceDark)
                                }
                            }
                        }
                    }

                    // Mission 4 Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = if (mSynced) CyberGreen else HighContrastGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Шаг 4: Синхронизировать узлы по P2P",
                                            fontWeight = FontWeight.Bold,
                                            color = AccentTeal,
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (mSynced) {
                                        Text("🏆 ВЫПОЛНЕНО", color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission4_done"))
                                    } else {
                                        Text("⏳ ОЖИДАЕТ", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.testTag("mission4_pending"))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Запустите протокол слияния данных, который опросит вашу overlay-почту или симулирует P2P-сеть для получения последних дельт репутации.",
                                    color = TextLight,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.triggerSync() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("trigger_sync_quest_btn"),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("📡 Запустить Мониторинг Сети", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                1 -> {
                    item {
                        Text(
                            "АРХИТЕКТУРНЫЕ ИНСТИТУТЫ (Social OS)",
                            style = MaterialTheme.typography.titleMedium,
                            color = HighContrastGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Система построена как ядро Linux: компактный набор прозрачных алгоритмических институтов (Code is Law), вокруг которых развертываются прикладные ветки-форки участников.",
                            color = TextLight,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(institutesData) { inst ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(inst.icon, contentDescription = inst.title, tint = AccentTeal, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(inst.title, fontWeight = FontWeight.Bold, color = AccentTeal, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(inst.desc, color = TextLight, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Логика Закона:", color = HighContrastGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(inst.rule, color = SoftCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        Text(
                            "КЛАССОВАЯ МОДЕЛЬ ООП (Чистая Архитектура)",
                            style = MaterialTheme.typography.titleMedium,
                            color = HighContrastGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Ниже приведен исходный код доменных сущностей и протоколов ядра, отражающих закон прямого репутационного народовластия.",
                            color = TextLight,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(oopModelData) { codeM ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF070A0F)),
                            border = BorderStroke(1.dp, CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(codeM.className, color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                                Text(codeM.meta, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                Text(
                                    codeM.methods,
                                    color = SoftCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier
                                        .background(Color.Black.copy(0.4f))
                                        .padding(6.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                3 -> {
                    item {
                        Text(
                            "P2P ЯДРО СИНХРОНИЗАЦИИ SMTP / IMAP",
                            style = MaterialTheme.typography.titleMedium,
                            color = HighContrastGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            "Для связи вне цензуры Открытый Орден использует зашифрованные SMTP/IMAP сообщения в качестве P2P транспорта. Каждый узел — это автономный почтовый клиент, а роль DNS-маршрутизаторов играет глобальная цепочка email-провайдеров.",
                            color = TextLight,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("💡 Стек Рекомендуемых Технологий Android:", fontWeight = FontWeight.Bold, color = AccentTeal, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                val techList = listOf(
                                    "СУБД SQLite/Room с поддержкой Git-лайк версионирования таблиц для безболезненных форков.",
                                    "Kamil (SMTP/IMAP Kotlin Multiplatform) для прямого коннекта к почтовым серверам.",
                                    "Sodium Oxide / Noise Protocol для сквозного шифрования (E2EE) писем.",
                                    "Automerge / Yoko CRDT для бесконфликтного слияния репутационных изменений.",
                                    "Ed25519 (ECDSA) для подписи каждого голоса и действия («Закон защищен подписью»)."
                                )
                                techList.forEach { tech ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("• ", color = AccentTeal, fontWeight = FontWeight.Bold)
                                        Text(tech, color = TextLight, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = CosmicGray)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🔄 Конфликтология Межсетевого Обмена:", fontWeight = FontWeight.Bold, color = AccentTeal, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Каждое решение (Proposal) пакуется как JSON, шифруется, подписывается асимметричным Ed25519-ключом узла и высылается как SMTP-конверт.\n\n" +
                                    "При получении (IMAP), узел верифицирует репутационный вес отправителя на момент отправки и накатывает дельту: если голоса перешли Рубикон, локальные триггеры генерируют задачи управления автоматически.",
                                    color = TextLight,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: COUNCIL (PROPOSALS) & EXECUTIVE (TASKS)
// -------------------------------------------------------------
@Composable
fun CouncilAndTasksTab(
    viewModel: SuiteViewModel,
    proposals: List<ProposalEntity>,
    tasks: List<TaskEntity>,
    currentAgentId: String,
    selectedForkId: String
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "СОВЕТ (COUNCIL/PARLIAMENT)",
                    style = MaterialTheme.typography.titleMedium,
                    color = HighContrastGold,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("create_proposal_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Создать", fontSize = 12.sp)
                }
            }
            Text(
                "Законодательная палата. Принятие повестки дня репутационным взвешенным голосованием.",
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (proposals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет активных предложений в этом форке.", color = TextMuted, fontSize = 13.sp)
                }
            }
        }

        items(proposals) { proposal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray),
                border = BorderStroke(
                    width = 1.dp,
                    color = when (proposal.status) {
                        "ACCEPTED" -> CyberGreen.copy(0.4f)
                        "REJECTED" -> AlertRed.copy(0.4f)
                        else -> AccentTeal.copy(0.2f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ID: ${proposal.id.uppercase(Locale.ROOT)}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (proposal.status) {
                                        "ACCEPTED" -> CyberGreen.copy(0.15f)
                                        "REJECTED" -> AlertRed.copy(0.15f)
                                        else -> AccentTeal.copy(0.15f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (proposal.status) {
                                    "ACCEPTED" -> "ПРИНЯТО (Law)"
                                    "REJECTED" -> "ОТКЛОНЕНО"
                                    else -> "ГОЛОСОВАНИЕ ACTIVE"
                                },
                                color = when (proposal.status) {
                                    "ACCEPTED" -> CyberGreen
                                    "REJECTED" -> AlertRed
                                    else -> AccentTeal
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(proposal.title, fontWeight = FontWeight.Bold, color = TextLight, fontSize = 14.sp)
                    Text(proposal.description, color = TextLight, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    val totalWeight = proposal.voteYesRep + proposal.voteNoRep
                    val yesProgress = if (totalWeight > 0) (proposal.voteYesRep / totalWeight).toFloat() else 0f
                    val quorumText = "Кворум: ${totalWeight.toInt()}/${proposal.quorumRequired.toInt()} REP"

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ЗА: ${proposal.voteYesRep.toInt()} REP (${(yesProgress * 100).toInt()}%)",
                                color = CyberGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ПРОТИВ: ${proposal.voteNoRep.toInt()} REP (${((1f - yesProgress) * 100).toInt()}%)",
                                color = AlertRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = yesProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberGreen,
                            trackColor = AlertRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = quorumText,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }

                    if (proposal.status == "ACTIVE") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.castVote(proposal.id, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen.copy(0.2f), contentColor = CyberGreen),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .testTag("vote_yes_${proposal.id}")
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ЗА", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.castVote(proposal.id, false) },
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(0.2f), contentColor = AlertRed),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("vote_no_${proposal.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ПРОТИВ", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION EXECUTIVE / TASKS ---
        item {
            SeparatorTitle("УПРАВА (EXECUTIVE/ADMINISTRATION)")
            Text(
                text = "Событийная модель: если повестка принята Советом, узел автоматически разворачивает задачу (Executive Task). Завершив ее, участник получает меритократическую репу.",
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Управа не содержит активных поручений.", color = TextMuted, fontSize = 12.sp)
                }
            }
        }

        items(tasks) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Награда: +${task.reputationReward.toInt()} REP",
                            color = HighContrastGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (task.status == "AUDITED") CyberGreen.copy(0.15f) else SoftCyan.copy(0.15f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (task.status == "AUDITED") "ВЫПОЛНЕНО & АУДИРОВАНО" else "НА ЭТАПЕ ВЫПОЛНЕНИЯ",
                                color = if (task.status == "AUDITED") CyberGreen else SoftCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(task.title, fontWeight = FontWeight.Bold, color = TextLight, fontSize = 14.sp)
                    Text(task.description, color = TextLight, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))

                    if (task.status != "AUDITED") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { viewModel.completeTask(task.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.align(Alignment.End).testTag("complete_task_${task.id}")
                        ) {
                            Text("Сделать Вклад (Закрыть задачу)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Освоено агентом: ${task.assignedTo ?: "Неизвестно"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = CosmicGray,
            title = { Text("Новое предложение в Совет", color = AccentTeal) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Название (Закон/Хартия)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_proposal_title")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Этическое / Содержательное обоснование", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("add_proposal_desc")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createProposal(newTitle, newDesc)
                        newTitle = ""
                        newDesc = ""
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    modifier = Modifier.testTag("add_proposal_submit")
                ) {
                    Text("Опубликовать", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Отмена", color = TextMuted)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 2: JUDICIARY / ARBITRATION (СУД & СПОРЫ)
// -------------------------------------------------------------
@Composable
fun JudiciaryTab(
    viewModel: SuiteViewModel,
    disputes: List<DisputeEntity>,
    agents: List<AgentEntity>,
    currentAgentId: String
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDefendant by remember { mutableStateOf("") }
    var lawsuitDesc by remember { mutableStateOf("") }
    var selectedArticle by remember { mutableStateOf("Раздел I. Отрез 1 (Национальный суверенитет)") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "СУД / АРБИТРАЖ",
                    style = MaterialTheme.typography.titleMedium,
                    color = HighContrastGold,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { 
                        selectedDefendant = agents.firstOrNull { it.id != currentAgentId }?.id ?: ""
                        showCreateDialog = true 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("raise_dispute_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Заявить иск", fontSize = 12.sp)
                }
            }
            Text(
                "Судебный надзор на основе этического кодекса. Присяжные созываются случайно из числа пиров с высокой репутацией.",
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (disputes.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Конституционная палата чиста от споров.", color = TextMuted)
                }
            }
        }

        items(disputes) { dispute ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray),
                border = BorderStroke(
                    1.dp,
                    if (dispute.status == "RESOLVED_YES") AlertRed.copy(0.4f) else AccentTeal.copy(0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "КОДЕКС: ${dispute.constitutionalArticle}",
                            color = HighContrastGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (dispute.status) {
                                        "RESOLVED_YES" -> AlertRed.copy(0.15f)
                                        "RESOLVED_NO" -> CyberGreen.copy(0.15f)
                                        else -> AccentTeal.copy(0.15f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when (dispute.status) {
                                    "RESOLVED_YES" -> "ВЕРДИКТ: ВИНОВЕН"
                                    "RESOLVED_NO" -> "ВЕРДИКТ: ОПРАВДАН"
                                    else -> "НА РАССМОТРЕНИИ"
                                },
                                color = when (dispute.status) {
                                    "RESOLVED_YES" -> AlertRed
                                    "RESOLVED_NO" -> CyberGreen
                                    else -> AccentTeal
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Истец: ${dispute.plaintiffId}  ->  Обвиняемый: ${dispute.defendantId}",
                        color = SoftCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = dispute.description,
                        color = TextLight,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Судейская Коллегия (Jury IDs): [${dispute.juryIds.replace(",", ", ")}]",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Решение суда: ${dispute.verdict}",
                        color = if (dispute.status == "RESOLVED_YES") AlertRed else SoftCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    val juryList = dispute.juryIds.split(",")
                    val isJuror = juryList.contains(currentAgentId)

                    if (dispute.status == "REVIEW" && isJuror) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Вы назначены судьей присяжных:",
                                color = HighContrastGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Button(
                                    onClick = { viewModel.castJuryVote(dispute.id, true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed.copy(0.2f), contentColor = AlertRed),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.padding(end = 4.dp).testTag("jury_guilty_${dispute.id}")
                                ) {
                                    Text("ВИНОВЕН", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.castJuryVote(dispute.id, false) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen.copy(0.2f), contentColor = CyberGreen),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("jury_innocent_${dispute.id}")
                                ) {
                                    Text("ОПРАВДАН", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = CosmicGray,
            title = { Text("Заявление Конституционного Иска", color = AccentTeal) },
            text = {
                Column {
                    Text("Выберите обвиняемого пира:", color = TextMuted, fontSize = 12.sp)
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = "$selectedDefendant ▾",
                            color = AccentTeal,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { expandedDropdown = true }
                                .background(SpaceDark)
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false },
                            modifier = Modifier.background(CosmicGray)
                        ) {
                            agents.filter { it.id != currentAgentId }.forEach { agent ->
                                DropdownMenuItem(
                                    text = { Text(agent.id, color = TextLight) },
                                    onClick = {
                                        selectedDefendant = agent.id
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Категория (Нарушенная статья):", color = TextMuted, fontSize = 12.sp)
                    var expandedArticles by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(
                            text = "$selectedArticle ▾",
                            color = AccentTeal,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { expandedArticles = true }
                                .background(SpaceDark)
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expandedArticles,
                            onDismissRequest = { expandedArticles = false },
                            modifier = Modifier.background(CosmicGray)
                        ) {
                            listOf(
                                "Раздел I. Отрез 1 (Конституционная неприкосновенность)",
                                "Раздел II. Статья 4 (Добросовестный вклад)",
                                "Раздел III. Параграф 1 (Защита от Сибилл-вмешательства)",
                                "Раздел V. Закон 12 (Идеологическая солидарность)"
                            ).forEach { art ->
                                DropdownMenuItem(
                                    text = { Text(art, color = TextLight) },
                                    onClick = {
                                        selectedArticle = art
                                        expandedArticles = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = lawsuitDesc,
                        onValueChange = { lawsuitDesc = it },
                        label = { Text("Описание инцидента и улик", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().height(80.dp).testTag("dispute_desc_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.raiseDispute(selectedDefendant, lawsuitDesc, selectedArticle)
                        lawsuitDesc = ""
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    modifier = Modifier.testTag("submit_dispute_btn")
                ) {
                    Text("Заявить Коллегии", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Отмена", color = TextMuted)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 3: REPOSITORY / FORKS & DETAILED SMTP NETWORK SYNC LOGS
// -------------------------------------------------------------
@Composable
fun RepositoryForksTab(
    viewModel: SuiteViewModel,
    forks: List<ForkEntity>,
    syncLogs: List<SyncLogEntity>,
    selectedForkId: String,
    agents: List<AgentEntity>
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var forkIdInput by remember { mutableStateOf("") }
    var forkTitleInput by remember { mutableStateOf("") }
    var forkDescInput by remember { mutableStateOf("") }
    var quorumMult by remember { mutableStateOf(1.0f) }
    var minRepThreshold by remember { mutableStateOf(10.0f) }

    // Participant invite / import state
    var peerShareInput by remember { mutableStateOf("") }
    var importedFeedback by remember { mutableStateOf("") }

    val activeSmtpHost by viewModel.smtpHost.collectAsState()
    val activeSmtpPort by viewModel.smtpPort.collectAsState()
    val activeImapHost by viewModel.imapHost.collectAsState()
    val activeImapPort by viewModel.imapPort.collectAsState()
    val activeMailUser by viewModel.mailUser.collectAsState()
    val activeMailPass by viewModel.mailPass.collectAsState()
    val activeUseSsl by viewModel.useSsl.collectAsState()

    var smtpHostInput by remember(activeSmtpHost) { mutableStateOf(activeSmtpHost) }
    var smtpPortInput by remember(activeSmtpPort) { mutableStateOf(activeSmtpPort.toString()) }
    var imapHostInput by remember(activeImapHost) { mutableStateOf(activeImapHost) }
    var imapPortInput by remember(activeImapPort) { mutableStateOf(activeImapPort.toString()) }
    var mailUserInput by remember(activeMailUser) { mutableStateOf(activeMailUser) }
    var mailPassInput by remember(activeMailPass) { mutableStateOf(activeMailPass) }
    var useSslInput by remember(activeUseSsl) { mutableStateOf(activeUseSsl) }

    val context = androidx.compose.ui.platform.LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray),
                border = BorderStroke(1.dp, HighContrastGold.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "РЕКРУТИНГ И ПРИГЛАШЕНИЯ (P2P ONBOARDING)",
                        color = HighContrastGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Отправьте инвайт новому участнику или вставьте его инвайт ниже для занесения в защищенную СУБД узла.",
                        color = TextLight,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Import row
                    OutlinedTextField(
                        value = peerShareInput,
                        onValueChange = { peerShareInput = it },
                        label = { Text("Код приглашения участника", color = TextMuted) },
                        placeholder = { Text("Вставьте код формата RECRUIT_JOIN#...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = AccentTeal,
                            unfocusedBorderColor = TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invite_importer_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val text = peerShareInput.trim()
                                if (text.startsWith("RECRUIT_JOIN#")) {
                                    try {
                                        val parts = text.removePrefix("RECRUIT_JOIN#").split("|")
                                        if (parts.size >= 4) {
                                            val id = parts[0]
                                            val name = parts[1]
                                            val role = parts[2]
                                            val rep = parts[3].toDoubleOrNull() ?: 20.0
                                            val key = if (parts.size > 4) parts[4] else ""
                                            viewModel.registerAgent(id, name, role, rep, key, autoLogin = false)
                                            importedFeedback = "Успешно импортирован участник: $name ($id)!"
                                            peerShareInput = ""
                                        } else {
                                            importedFeedback = "Ошибка разбора: неверный формат полей"
                                        }
                                    } catch (e: Exception) {
                                        importedFeedback = "Ошибка импорта: ${e.message}"
                                    }
                                } else {
                                    importedFeedback = "Ошибка: код должен начинаться с RECRUIT_JOIN#"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f).testTag("import_peer_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Внести пира", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val currentAgent = agents.find { it.id == viewModel.currentAgentId.value }
                                if (currentAgent != null) {
                                    val inviteCode = "RECRUIT_JOIN#${currentAgent.id}|${currentAgent.name}|${currentAgent.role}|${currentAgent.reputationScore}|${currentAgent.publicKey}"
                                    try {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("OpenOrdenInvite", inviteCode)
                                        clipboard.setPrimaryClip(clip)
                                        importedFeedback = "Ваш инвайт-код скопирован в буфер! Передайте его новому участнику боевой системы."
                                    } catch (e: Exception) {
                                        importedFeedback = "Ошибка буфера: ${e.message}. Код: $inviteCode"
                                    }
                                } else {
                                    importedFeedback = "Сначала выберите или зарегистрируйте свой аккаунт!"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicGray, contentColor = HighContrastGold),
                            border = BorderStroke(1.dp, HighContrastGold),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f).testTag("copy_my_invite_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = HighContrastGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Поделиться собой", fontSize = 11.sp, color = HighContrastGold)
                        }
                    }

                    if (importedFeedback.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = importedFeedback,
                            color = CyberGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            var showSettings by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicGray),
                border = BorderStroke(1.dp, AccentTeal.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = AccentTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "НАСТРОЙКИ РЕАЛЬНОГО P2P SMTP / IMAP КАНАЛА",
                                color = AccentTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        TextButton(
                            onClick = { showSettings = !showSettings },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (showSettings) "Скрыть" else "Настроить",
                                color = HighContrastGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showSettings) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Для связи вне цензуры на реальных SMTP/IMAP серверах, укажите параметры своего почтового ящика ниже. Рекомендуется использовать специальный пароль приложения (App Password).",
                            color = TextLight,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Host Row SMTP
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = smtpHostInput,
                                onValueChange = { smtpHostInput = it },
                                label = { Text("SMTP Хост", color = TextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted
                                ),
                                modifier = Modifier.weight(2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = smtpPortInput,
                                onValueChange = { smtpPortInput = it },
                                label = { Text("Порт", color = TextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Host Row IMAP
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = imapHostInput,
                                onValueChange = { imapHostInput = it },
                                label = { Text("IMAP Хост", color = TextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted
                                ),
                                modifier = Modifier.weight(2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = imapPortInput,
                                onValueChange = { imapPortInput = it },
                                label = { Text("Порт", color = TextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = AccentTeal,
                                    unfocusedBorderColor = TextMuted
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Credentials
                        OutlinedTextField(
                            value = mailUserInput,
                            onValueChange = { mailUserInput = it },
                            label = { Text("Email (Адрес ноды)", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = AccentTeal,
                                unfocusedBorderColor = TextMuted
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = mailPassInput,
                            onValueChange = { mailPassInput = it },
                            label = { Text("Пароль / Пароль приложения", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = AccentTeal,
                                unfocusedBorderColor = TextMuted
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // SSL use checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = useSslInput,
                                onCheckedChange = { useSslInput = it },
                                colors = CheckboxDefaults.colors(checkedColor = AccentTeal)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Использовать SSL/TLS шифрование транспортного уровня", color = TextLight, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateMailSettings(
                                        smtpH = smtpHostInput.trim(),
                                        smtpP = smtpPortInput.trim().toIntOrNull() ?: 465,
                                        imapH = imapHostInput.trim(),
                                        imapP = imapPortInput.trim().toIntOrNull() ?: 993,
                                        usr = mailUserInput.trim(),
                                        pas = mailPassInput,
                                        ssl = useSslInput
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Сохранить", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.testMailConnection()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicGray, contentColor = HighContrastGold),
                                border = BorderStroke(1.dp, HighContrastGold),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Проверить линк", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HighContrastGold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "РЕПОЗИТОРИЙ & ФОРКИ (FORK SYSTEM)",
                    style = MaterialTheme.typography.titleMedium,
                    color = HighContrastGold,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("create_fork_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Форкнуть", fontSize = 12.sp)
                }
            }
            Text(
                "Если пиров не устраивает регламент ядра, они создают 'Форк' (Ветвь законов). Это оберегает Орден от окаменения.",
                color = TextLight,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        item {
            Text(
                "Доступные Конституции на узле (Выбрать активную ветвь):",
                color = HighContrastGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(forks) { fork ->
            val isSelected = fork.id == selectedForkId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.selectFork(fork.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) AccentTeal.copy(0.1f) else CosmicGray
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) AccentTeal else Color.Transparent
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fork.title,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) AccentTeal else TextLight,
                            fontSize = 14.sp
                        )
                        if (fork.parentForkId != null) {
                            Text(
                                "Родитель: [${fork.parentForkId}]",
                                color = SoftCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Text("Базовый куратор", color = CyberGreen, fontSize = 10.sp)
                        }
                    }
                    Text(fork.description, color = TextLight, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Множитель кворума: x${fork.votingQuorumMultiplier}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            "Порог автора: ${fork.minReputationToPropose.toInt()} REP",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        item {
            SeparatorTitle("СИНХРОНИЗАЦИЯ P2P (SMTP / IMAP ОВЕРЛЕЙ)")
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Каждое действие асинхронно упаковывается в email-конверты, шифруется, подписывается и вещается пирам по свободным SMTP реле.",
                    color = TextLight,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.triggerSync() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = SpaceDark),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 8.dp).testTag("simulate_sync_action")
                ) {
                    Text("Синхронизировать", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (syncLogs.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("Сеть молчит. Выполните действия в Сандбоксе для генерации писем.", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        items(syncLogs) { log ->
            val isSmtp = log.direction == "SMTP_SEND"
            val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val timeStr = format.format(Date(log.timestamp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, CosmicGray)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSmtp) Icons.Default.Send else Icons.Default.Email,
                                contentDescription = null,
                                tint = if (isSmtp) AccentTeal else CyberGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSmtp) "P2P TRANSMIT (SMTP SEND)" else "P2P CONSUME (IMAP RECEIVE)",
                                color = if (isSmtp) AccentTeal else CyberGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(timeStr, color = TextMuted, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HASH: ${log.messageHash}  •  STATUS: ${log.statusUrl}",
                        color = HighContrastGold,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = log.mailEnvelope,
                        color = TextLight,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(SpaceDark)
                            .padding(6.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = CosmicGray,
            title = { Text("Создать Форк Законов (Версию Системы)", color = AccentTeal) },
            text = {
                Column {
                    OutlinedTextField(
                        value = forkIdInput,
                        onValueChange = { forkIdInput = it },
                        label = { Text("Идентификатор (e.g. libertarian-v2)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("fork_id_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = forkTitleInput,
                        onValueChange = { forkTitleInput = it },
                        label = { Text("Название Концепта (Плана)", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("fork_title_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = forkDescInput,
                        onValueChange = { forkDescInput = it },
                        label = { Text("Законодательное отличие от Ядра", color = TextMuted) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedContainerColor = SpaceDark,
                            unfocusedContainerColor = SpaceDark,
                            focusedIndicatorColor = AccentTeal
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp).testTag("fork_desc_input")
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Множитель Кворума: x${String.format(java.util.Locale.US, "%.1f", quorumMult)}", color = TextLight, fontSize = 12.sp)
                    Slider(
                        value = quorumMult,
                        onValueChange = { quorumMult = it },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(thumbColor = AccentTeal, activeTrackColor = AccentTeal)
                    )

                    Text("Порог подачи предложения: ${minRepThreshold.toInt()} REP", color = TextLight, fontSize = 12.sp)
                    Slider(
                        value = minRepThreshold,
                        onValueChange = { minRepThreshold = it },
                        valueRange = 5.0f..50.0f,
                        colors = SliderDefaults.colors(thumbColor = AccentTeal, activeTrackColor = AccentTeal)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createFork(
                            forkIdInput,
                            forkTitleInput,
                            forkDescInput,
                            quorumMult.toDouble(),
                            minRepThreshold.toDouble()
                        )
                        forkIdInput = ""
                        forkTitleInput = ""
                        forkDescInput = ""
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentTeal, contentColor = SpaceDark),
                    modifier = Modifier.testTag("submit_fork_btn")
                ) {
                    Text("Инициализировать Форк", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Отмена", color = TextMuted)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// UI HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
fun SeparatorTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = HighContrastGold,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(color = CosmicGray, thickness = 1.dp, modifier = Modifier.weight(1f))
    }
}

// -------------------------------------------------------------
// DESCRIPTIVE METADATA STRUCTS IN RUSSIAN
// -------------------------------------------------------------
data class InstituteDisplayInfo(
    val title: String,
    val icon: ImageVector,
    val desc: String,
    val rule: String
)

val institutesData = listOf(
    InstituteDisplayInfo(
        title = "1. Identity & Agent",
        icon = Icons.Default.AccountCircle,
        desc = "Учетная запись участника. Хранит историю зафиксированного вклада в Орден и репутационный вес (Reputation Score). Пресекает анонимный деструктив.",
        rule = "Agent(id, reputationScore) -> Weight = reputationScore"
    ),
    InstituteDisplayInfo(
        title = "2. Council & Parliament",
        icon = Icons.Default.List,
        desc = "Законодательный созываемый Совет пиров. Формирует повестку дня общества. Одобренные репутационным голосованием Хартии вступают в силу автоматически.",
        rule = "if (yesVotesRep > noVotesRep && totalRep >= quorumRequired) ACCEPT"
    ),
    InstituteDisplayInfo(
        title = "3. Judiciary & Arbitration",
        icon = Icons.Default.Warning,
        desc = "Судебная власть Ордена. Разрешает внутренние конфликты на базе неизменного Кодекса Конституции. Судьи присяжных отбираются случайно во избежание подкупа.",
        rule = "selectJury(candidates, 2) -> randomized eligible members >= 50 REP"
    ),
    InstituteDisplayInfo(
        title = "4. Executive & Administration",
        icon = Icons.Default.Build,
        desc = "Исполнительная Управа участников. Набирает роли под конкретные задачи. Задачи создаются автоматически как продолжение принятых Советом законов.",
        rule = "onProposalApproved() -> trigger autoCreateTask() -> awardRepOnComplete()"
    ),
    InstituteDisplayInfo(
        title = "5. Election & Voting",
        icon = Icons.Default.CheckCircle,
        desc = "Избирательная комиссия и Сибил-протокол. Предотвращает спам-голосования виртуалами. Игнорирует голоса ботов ниже определенного reputational ценза.",
        rule = "validateVote(voter) -> return voter.reputationScore >= SybilThreshold"
    ),
    InstituteDisplayInfo(
        title = "6. Repository & Fork",
        icon = Icons.Default.Share,
        desc = "Архивация правил и Древо ветвлений. Код Ордена может быть форкнут в любую секунду, дабы не дать системе заржаветь и законсервировать элиту.",
        rule = "forkRepository(parentId, paramAdjustments) -> spawn parallel instance"
    )
)

data class OopModelDisplayInfo(
    val className: String,
    val meta: String,
    val methods: String
)

val oopModelData = listOf(
    OopModelDisplayInfo(
        className = "class Agent",
        meta = "Хранилище репутации участника",
        methods = "fun calculateVoteWeight(): Double = reputationScore\n" +
                "fun registerContribution(c: Contribution): Agent"
    ),
    OopModelDisplayInfo(
        className = "class Proposal & Council",
        meta = "Управление голосованием Советом",
        methods = "fun castVote(voter: Agent, approve: Boolean)\n" +
                "fun checkQuorumReached(): Boolean\n" +
                "fun executeLaw(): Task  // Trigger automatic executive event"
    ),
    OopModelDisplayInfo(
        className = "class Dispute & JudiciaryStore",
        meta = "Разрешение нарушений Кодекса",
        methods = "fun selectJuryMembers(peers: List<Agent>): List<Agent>\n" +
                "fun evaluateVerdict(): DisputeStatus\n" +
                "fun enforcePenalty(): Agent  // Deduct bad reputations"
    ),
    OopModelDisplayInfo(
        className = "class ExecutiveEngine",
        meta = "Автореализация принятых законов",
        methods = "fun createTaskFromProposal(p: Proposal): Task\n" +
                "fun auditAndAwardReputation(t: Task, auditor: Agent): Agent"
    ),
    OopModelDisplayInfo(
        className = "class RepositoryForkManager",
        meta = "Контроль нелинейной эволюции системы",
        methods = "fun createVersionFork(p: SystemFork, newMultiplier: Double): SystemFork\n" +
                "fun mergeDeltas(crdtState1: Table, crdtState2: Table): Table"
    )
)
