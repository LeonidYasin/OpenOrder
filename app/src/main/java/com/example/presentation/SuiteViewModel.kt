package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SuiteViewModel(application: Application) : AndroidViewModel(application) {

    private val db: OrdenDatabase = Room.databaseBuilder(
        application.applicationContext,
        OrdenDatabase::class.java,
        "orden_social_os_v3.db"
    ).fallbackToDestructiveMigration().build()

    val repository = OrdenRepository(db.ordenDao())

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

    fun writeConsole(message: String) {
        _consoleOutput.value = ">> $message\n${_consoleOutput.value.take(400)}"
    }

    // Interactive operations
    fun castVote(proposalId: String, votedYes: Boolean) {
        viewModelScope.launch {
            val response = repository.castVote(proposalId, _currentAgentId.value, votedYes)
            writeConsole(response)
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
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val response = repository.completeTask(taskId, _currentAgentId.value)
            writeConsole(response)
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
        viewModelScope.launch {
            writeConsole("Запуск опроса P2P SMTP/IMAP ящиков сетевых участников...")
            val response = repository.triggerNetworkSyncDemo()
            writeConsole(response)
        }
    }
}
