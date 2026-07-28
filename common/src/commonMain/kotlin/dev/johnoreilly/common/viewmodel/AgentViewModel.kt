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

    // Cached player list, used to enrich answers with player cards
    private var players: List<Player>? = null

    /** Players referenced in [text] by full name or surname, in order of appearance (max 5). */
    private suspend fun playersMentionedIn(text: String): List<Player> {
        val all = players ?: runCatching { repository.getPlayers().first() }.getOrNull().orEmpty()
            .also { players = it }
        return all.map { it to matchIndex(text, it) }
            .filter { it.second >= 0 }
            .distinctBy { it.first.id }
            .sortedBy { it.second }
            .map { it.first }
            .take(5)
    }

    /** Index of the first match of the player's full name, else its surname as a whole word; -1 if none. */
    private fun matchIndex(text: String, player: Player): Int {
        val full = text.indexOf(player.name, ignoreCase = true)
        if (full >= 0) return full
        val surname = player.name.substringAfterLast(' ')
        // Only fall back to surname if it's distinctive enough to avoid false positives
        return if (surname.length >= 4) wholeWordIndex(text, surname) else -1
    }

    /** Index of [word] in [text] where it isn't part of a longer word; -1 if absent. KMP-safe (no regex). */
    private fun wholeWordIndex(text: String, word: String): Int {
        var from = 0
        while (true) {
            val i = text.indexOf(word, from, ignoreCase = true)
            if (i < 0) return -1
            val okBefore = i == 0 || !text[i - 1].isLetter()
            val end = i + word.length
            val okAfter = end >= text.length || !text[end].isLetter()
            if (okBefore && okAfter) return i
            from = i + 1
        }
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
                    onAssistantMessage = { message ->
                        val mentioned = playersMentionedIn(message)
                        _uiState.update {
                            it.copy(
                                messages = it.messages + Message.AgentMessage(message, mentioned),
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
