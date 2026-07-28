package dev.johnoreilly.common.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel

expect fun getLLModel(): LLModel
expect fun getPromptExecutor(): PromptExecutor

/**
 * Factory for the Fantasy Premier League Koog agent. The callbacks let the UI stream
 * intermediate events (tool calls, errors) and drive a multi-turn conversation.
 */
interface AgentProvider {
    val description: String

    suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (String) -> String
    ): AIAgent<String, String>
}
