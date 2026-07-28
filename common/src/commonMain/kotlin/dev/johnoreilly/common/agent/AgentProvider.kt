package dev.johnoreilly.common.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import kotlinx.serialization.Serializable

expect fun getLLModel(): LLModel
expect fun getPromptExecutor(): PromptExecutor

/**
 * Structured assistant answer. The model fills [playerIds] with the exact FPL ids of any
 * players it referenced, so the UI can render player cards deterministically (no name matching).
 */
@Serializable
@LLMDescription("A Fantasy Premier League assistant answer")
data class FplAnswer(
    @property:LLMDescription("The user-facing answer, formatted as markdown")
    val text: String,
    @property:LLMDescription("The FPL 'id' of every player referenced in the answer, to display as cards; empty if none")
    val playerIds: List<Int> = emptyList(),
)

/**
 * Factory for the Fantasy Premier League Koog agent. The callbacks let the UI stream
 * intermediate events (tool calls, errors) and drive a multi-turn conversation.
 */
interface AgentProvider {
    val description: String

    suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (FplAnswer) -> String
    ): AIAgent<String, String>
}
