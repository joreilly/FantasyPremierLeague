package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.agent.AgentProvider
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Chat message types
sealed class Message {
    data class UserMessage(val text: String) : Message()
    // An agent answer, optionally enriched with players it referenced (rendered as cards)
    data class AgentMessage(val text: String, val players: List<Player> = emptyList()) : Message()
    data class SystemMessage(val text: String) : Message()
    data class ErrorMessage(val text: String) : Message()
    data class ToolCallMessage(val text: String) : Message()
    data class ResultMessage(val text: String) : Message()
}

data class AgentUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "Who are the top scoring players?",
    val isInputEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val isChatEnded: Boolean = false,

    // For handling user responses when the agent asks a follow-up question
    val userResponseRequested: Boolean = false,
    val currentUserResponse: String? = null,
)

class AgentViewModel(
    private val agentProvider: AgentProvider,
    private val repository: FantasyPremierLeagueRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AgentUiState(messages = listOf(Message.SystemMessage(agentProvider.description)))
    )
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    // Cached player list, used to resolve the ids the agent returns into player cards
    private var players: List<Player>? = null

    /** Resolve the agent-provided player [ids] to players, preserving the agent's order (max 5). */
    private suspend fun playersByIds(ids: List<Int>): List<Player> {
        if (ids.isEmpty()) return emptyList()
        val all = players ?: runCatching { repository.getPlayers().first() }.getOrNull().orEmpty()
            .also { players = it }
        val byId = all.associateBy { it.id }
        return ids.mapNotNull { byId[it] }.take(5)
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val userInput = _uiState.value.inputText.trim()
        if (userInput.isEmpty()) return

        if (_uiState.value.userResponseRequested) {
            // Deliver the reply to a suspended agent question
            _uiState.update {
                it.copy(
                    messages = it.messages + Message.UserMessage(userInput),
                    inputText = "",
                    isLoading = true,
                    userResponseRequested = false,
                    currentUserResponse = userInput
                )
            }
        } else {
            // First message — kick off the agent
            _uiState.update {
                it.copy(
                    messages = it.messages + Message.UserMessage(userInput),
                    inputText = "",
                    isInputEnabled = false,
                    isLoading = true
                )
            }
            viewModelScope.launch { runAgent(userInput) }
        }
    }

    private suspend fun runAgent(userInput: String) {
        withContext(Dispatchers.Default) {
            try {
                val agent = agentProvider.provideAgent(
                    onToolCallEvent = { message ->
                        viewModelScope.launch {
                            _uiState.update { it.copy(messages = it.messages + Message.ToolCallMessage(message)) }
                        }
                    },
                    onErrorEvent = { errorMessage ->
                        viewModelScope.launch {
                            _uiState.update {
                                it.copy(
                                    messages = it.messages + Message.ErrorMessage(errorMessage),
                                    isInputEnabled = true,
                                    isLoading = false
                                )
                            }
                        }
                    },
                    onAssistantMessage = { answer ->
                        val cards = playersByIds(answer.playerIds)
                        _uiState.update {
                            it.copy(
                                messages = it.messages + Message.AgentMessage(answer.text, cards),
                                isInputEnabled = true,
                                isLoading = false,
                                userResponseRequested = true
                            )
                        }

                        val userResponse = _uiState
                            .first { it.currentUserResponse != null }
                            .currentUserResponse
                            ?: throw IllegalArgumentException("User response is null")

                        _uiState.update { it.copy(currentUserResponse = null) }
                        userResponse
                    },
                )

                val result = agent.run(userInput)

                _uiState.update {
                    it.copy(
                        messages = it.messages +
                                Message.ResultMessage(result) +
                                Message.SystemMessage("The agent has stopped."),
                        isInputEnabled = false,
                        isLoading = false,
                        isChatEnded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        messages = it.messages + Message.ErrorMessage("Error: ${e.message}"),
                        isInputEnabled = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun restartChat() {
        _uiState.update {
            AgentUiState(messages = listOf(Message.SystemMessage(agentProvider.description)))
        }
    }
}
