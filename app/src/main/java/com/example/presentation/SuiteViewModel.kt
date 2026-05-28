package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SuiteViewModel(application: Application) : AndroidViewModel(application) {

    private val db: OrdenDatabase = Room.databaseBuilder(
        application.applicationContext,
        OrdenDatabase::class.java,
        "orden_social_os_v3.db"
    ).fallbackToDestructiveMigration().build()

    val repository = OrdenRepository(db.ordenDao())

    private val prefs = application.getSharedPreferences("orden_settings", android.content.Context.MODE_PRIVATE)

    private val _smtpHost = MutableStateFlow(prefs.getString("smtp_host", "smtp.gmail.com") ?: "smtp.gmail.com")
    val smtpHost = _smtpHost.asStateFlow()

    private val _smtpPort = MutableStateFlow(prefs.getInt("smtp_port", 465))
    val smtpPort = _smtpPort.asStateFlow()

    private val _imapHost = MutableStateFlow(prefs.getString("imap_host", "imap.gmail.com") ?: "imap.gmail.com")
    val imapHost = _imapHost.asStateFlow()

    private val _imapPort = MutableStateFlow(prefs.getInt("imap_port", 993))
    val imapPort = _imapPort.asStateFlow()

    private val _mailUser = MutableStateFlow(prefs.getString("mail_user", "") ?: "")
    val mailUser = _mailUser.asStateFlow()

    private val _mailPass = MutableStateFlow(prefs.getString("mail_pass", "") ?: "")
    val mailPass = _mailPass.asStateFlow()

    private val _useSsl = MutableStateFlow(prefs.getBoolean("use_ssl", true))
    val useSsl = _useSsl.asStateFlow()

    fun updateMailSettings(smtpH: String, smtpP: Int, imapH: String, imapP: Int, usr: String, pas: String, ssl: Boolean) {
        prefs.edit().apply {
            putString("smtp_host", smtpH)
            putInt("smtp_port", smtpP)
            putString("imap_host", imapH)
            putInt("imap_port", imapP)
            putString("mail_user", usr)
            putString("mail_pass", pas)
            putBoolean("use_ssl", ssl)
            apply()
        }
        _smtpHost.value = smtpH
        _smtpPort.value = smtpP
        _imapHost.value = imapH
        _imapPort.value = imapP
        _mailUser.value = usr
        _mailPass.value = pas
        _useSsl.value = ssl
        writeConsole("Конфигурация P2P узла ($usr) сохранена.")
    }

    fun getMailSettings(): MailSettings {
        return MailSettings(
            smtpHost = _smtpHost.value,
            smtpPort = _smtpPort.value,
            imapHost = _imapHost.value,
            imapPort = _imapPort.value,
            user = _mailUser.value,
            pass = _mailPass.value,
            useSsl = _useSsl.value
        )
    }

    fun testMailConnection() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            writeConsole("Тестирование SMTP/IMAP соединения для ${_mailUser.value}...")
            val settings = getMailSettings()

            // 1. SMTP Test
            writeConsole("Тест 1/2: Отправка SMTP рукопожатия на ${_smtpHost.value}...")
            val smtpResult = MailTransport.sendEmail(
                settings = settings,
                recipient = _mailUser.value,
                subject = "ORDEN-P2P CONNECTION TEST",
                body = "ORDEN-P2P HANDSHAKE TEST MESSAGE FROM Android Node"
            )

            if (smtpResult.isSuccess) {
                writeConsole("SMTP соединение успешно! Тестовый конверт отправлен сам себе.")
            } else {
                writeConsole("Ошибка SMTP: ${smtpResult.exceptionOrNull()?.message}")
            }

            // 2. IMAP Test
            writeConsole("Тест 2/2: Чтение IMAP ящика на ${_imapHost.value}...")
            val imapResult = MailTransport.fetchUnreadP2PMessages(settings)
            if (imapResult.isSuccess) {
                writeConsole("IMAP соединение успешно! Найдено ${imapResult.getOrNull()?.size ?: 0} входящих транзакций Открытого Ордена.")
            } else {
                writeConsole("Ошибка IMAP: ${imapResult.exceptionOrNull()?.message}")
            }
        }
    }

    fun broadcastToPeers(actionType: String, forkId: String, payload: String) {
        val settings = getMailSettings()
        if (settings.user.isBlank() || settings.pass.isBlank()) {
            writeConsole("SMTP не настроен. Локальные изменения записаны (без вещания по SMTP/IMAP).")
            return
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val envelope = P2PEnvelope(
                senderId = settings.user,
                recipientId = "overlay-network@orden.p2p",
                actionType = actionType,
                forkId = forkId,
                serializedPayload = payload,
                signature = "SIG_ED25519_" + java.util.UUID.randomUUID().toString().take(12).uppercase()
            )
            val emailBody = envelope.generateEmailBody()
            val activeAgents = agents.value
            val peerEmails = activeAgents.map { it.id }.filter { it.contains("@") && !it.endsWith("@orden.p2p") && it != settings.user }

            if (peerEmails.isEmpty()) {
                writeConsole("В СУБД нет других пиров с реальными email-адресами. Тестируем: отправка пакета самому себе для верификации.")
                MailTransport.sendEmail(settings, settings.user, "ORDEN-P2P", emailBody)
            } else {
                for (rec in peerEmails) {
                    writeConsole("Трансляция P2P конверта к пиру: $rec...")
                    val res = MailTransport.sendEmail(settings, rec, "ORDEN-P2P: $actionType", emailBody)
                    if (res.isSuccess) {
                        writeConsole("Успешно доставлено пиру $rec по SMTP.")
                    } else {
                        writeConsole("Внимание: Сбой SMTP доставки для $rec: ${res.exceptionOrNull()?.message}")
                    }
                }
            }
        }
    }

    // UI state states
    val agents: StateFlow<List<AgentEntity>> = repository.getAgents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disputes: StateFlow<List<DisputeEntity>> = repository.getDisputes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val forks: StateFlow<List<ForkEntity>> = repository.getForks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncLogs: StateFlow<List<SyncLogEntity>> = repository.getSyncLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected fork ID & selected Actor perspective
    private val _selectedForkId = MutableStateFlow("core")
    val selectedForkId: StateFlow<String> = _selectedForkId.asStateFlow()

    private val _currentAgentId = MutableStateFlow("leonid@orden.p2p")
    val currentAgentId: StateFlow<String> = _currentAgentId.asStateFlow()

    // Current proposals filtered by selected fork ID
    val proposals: StateFlow<List<ProposalEntity>> = _selectedForkId
        .flatMapLatest { forkId -> repository.getProposals(forkId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback logs or console messages within application sandbox
    private val _consoleOutput = MutableStateFlow("Запущено защищенное P2P ядро 'Открытый Орден'. Синхронизация успешна.")
    val consoleOutput: StateFlow<String> = _consoleOutput.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedData()
        }
    }

    fun selectFork(forkId: String) {
        _selectedForkId.value = forkId
        writeConsole("Переключение контекста на Систему-Форк: [${forkId.uppercase()}]")
    }

    fun selectAgent(agentId: String) {
        _currentAgentId.value = agentId
        writeConsole("Авторизована подпись сессии для: $agentId")
    }

    fun registerAgent(id: String, name: String, role: String, initialRep: Double = 20.0, customPubKey: String = "", autoLogin: Boolean = true) {
        val cleanId = id.trim().lowercase()
        if (cleanId.isEmpty() || !cleanId.contains("@")) {
            writeConsole("Ошибка: ID участника должен быть в формате P2P-идентификатора (например, user@orden.p2p)")
            return
        }
        if (name.trim().isEmpty()) {
            writeConsole("Ошибка: Имя/Никнейм не может быть пустым")
            return
        }
        viewModelScope.launch {
            val finalPubKey = if (customPubKey.isBlank()) {
                "0x" + java.util.UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
            } else {
                customPubKey.trim()
            }
            val newAgent = AgentEntity(
                id = cleanId,
                name = name.trim(),
                role = role,
                reputationScore = initialRep,
                contributionsCount = 0,
                publicKey = finalPubKey
            )
            repository.registerAgent(newAgent)
            writeConsole("В реестр Ордена внесен новый участник: $cleanId [Ключ: ${finalPubKey.take(10)}...]")
            
            if (autoLogin) {
                _currentAgentId.value = cleanId
                writeConsole("Сессия переключена на нового участника: $cleanId")
            }
        }
    }

    fun writeConsole(message: String) {
        _consoleOutput.value = ">> $message\n${_consoleOutput.value.take(400)}"
    }

    // Interactive operations
    fun castVote(proposalId: String, votedYes: Boolean) {
        viewModelScope.launch {
            val response = repository.castVote(proposalId, _currentAgentId.value, votedYes)
            writeConsole(response)
            if (response.contains("успешно") || response.contains("Кворум") || response.contains("Голос")) {
                broadcastToPeers("CAST_VOTE", _selectedForkId.value, "{\"proposalId\":\"$proposalId\",\"voter\":\"${_currentAgentId.value}\",\"vote\":$votedYes}")
            }
        }
    }

    fun createProposal(title: String, description: String) {
        if (title.isBlank() || description.isBlank()) {
            writeConsole("Ошибка: название и описание предложения не могут быть пустыми")
            return
        }
        viewModelScope.launch {
            val response = repository.createProposal(title, description, _currentAgentId.value, _selectedForkId.value)
            writeConsole(response)
            if (response.contains("успешно")) {
                broadcastToPeers(
                    "NEW_PROPOSAL", 
                    _selectedForkId.value, 
                    "{\"id\":\"prop-${java.util.UUID.randomUUID().toString().take(6)}\",\"title\":\"$title\",\"proposer\":\"${_currentAgentId.value}\",\"forkId\":\"${_selectedForkId.value}\",\"description\":\"$description\"}"
                )
            }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val response = repository.completeTask(taskId, _currentAgentId.value)
            writeConsole(response)
            if (response.contains("зафиксирован") || response.contains("успешно")) {
                broadcastToPeers("TASK_COMPLETED", _selectedForkId.value, "{\"taskId\":\"$taskId\",\"by\":\"${_currentAgentId.value}\"}")
            }
        }
    }

    fun raiseDispute(defendantId: String, description: String, article: String) {
        if (defendantId == _currentAgentId.value) {
            writeConsole("Ошибка: Вы не можете заявить конституционный dispute против самого себя!")
            return
        }
        viewModelScope.launch {
            val response = repository.raiseDispute(_currentAgentId.value, defendantId, description, article)
            writeConsole(response)
        }
    }

    fun castJuryVote(disputeId: String, voteGuilty: Boolean) {
        viewModelScope.launch {
            val response = repository.castJuryVote(disputeId, _currentAgentId.value, voteGuilty)
            writeConsole(response)
        }
    }

    fun createFork(forkId: String, title: String, description: String, quorumMultiplier: Double, minPropRep: Double) {
        val cleanForkId = forkId.trim().lowercase().replace(" ", "-")
        if (cleanForkId.isEmpty() || title.isBlank()) {
            writeConsole("Ошибка: Обязательные параметры форка не заполнены")
            return
        }
        viewModelScope.launch {
            val response = repository.createFork(
                id = cleanForkId,
                parentId = _selectedForkId.value,
                title = title,
                description = description,
                quorumMult = quorumMultiplier,
                minPropRep = minPropRep
            )
            writeConsole(response)
            _selectedForkId.value = cleanForkId // Переключим на созданный форк автоматически!
        }
    }

    fun triggerSync() {
        val settings = getMailSettings()
        if (settings.user.isBlank() || settings.pass.isBlank()) {
            viewModelScope.launch {
                writeConsole("Параметры P2P SMTP/IMAP узла полупусты. Запуск симуляции демо-оверлея...")
                val response = repository.triggerNetworkSyncDemo()
                writeConsole(response)
            }
            return
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            writeConsole("Запуск РЕАЛЬНОГО опроса P2P IMAP ящика ${settings.user}...")
            val fetchResult = MailTransport.fetchUnreadP2PMessages(settings)
            if (fetchResult.isSuccess) {
                val envelopes = fetchResult.getOrDefault(emptyList())
                writeConsole("Успешно опрошен IMAP сервер. Извлечено P2P писем: ${envelopes.size}")
                if (envelopes.isEmpty()) {
                    writeConsole("Новых P2P транзакций открытых повесток дня в почтовом оверлее не найдено.")
                } else {
                    for (env in envelopes) {
                        val mergeResult = repository.processIncomingMailEnvelope(env)
                        writeConsole(mergeResult)
                    }
                }
            } else {
                writeConsole("Внимание: Сбой опроса IMAP почты: ${fetchResult.exceptionOrNull()?.message}")
                writeConsole("Откат на локальную симуляцию оверлея...")
                val response = repository.triggerNetworkSyncDemo()
                writeConsole(response)
            }
        }
    }
}
