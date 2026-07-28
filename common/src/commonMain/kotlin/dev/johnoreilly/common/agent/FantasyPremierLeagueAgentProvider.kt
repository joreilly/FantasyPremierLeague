package dev.johnoreilly.common.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.prompt
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository

class FantasyPremierLeagueAgentProvider(
    private val repository: FantasyPremierLeagueRepository
) : AgentProvider {

    override val description: String =
        "Hi, I'm a Fantasy Premier League assistant. Ask me about players, fixtures and teams."

    override suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (String) -> String,
    ): AIAgent<String, String> {

        val toolRegistry = ToolRegistry {
            tool(GetPlayersTool(repository))
            tool(GetFixturesTool(repository))
        }

        val agentConfig = AIAgentConfig(
            prompt = prompt("fantasyPremierLeague") {
                system(
                    """
                    You are an AI assistant specialising in the Fantasy Premier League competition.
                    Only use the tools provided to get player and fixture data.
                    Keep answers concise and, where useful, present players or fixtures as a short list.
                    """.trimIndent(),
                )
            },
            model = getLLModel(),
            maxAgentIterations = 50
        )

        return AIAgent(
            promptExecutor = getPromptExecutor(),
            strategy = createStrategy(onAssistantMessage),
            agentConfig = agentConfig,
            toolRegistry = toolRegistry,
        ) {
            install(EventHandler) {
                onToolCallStarting { ctx ->
                    onToolCallEvent("Tool ${ctx.toolName}, args ${ctx.toolArgs}")
                }
                onAgentExecutionFailed { ctx ->
                    onErrorEvent("${ctx.error.message}")
                }
            }
        }
    }

    /**
     * Two-level conversation strategy: the outer loop continues while the user keeps
     * replying (an empty reply ends the chat); the inner loop runs agentic tool calls,
     * feeding results back to the LLM until it produces a plain text answer.
     */
    private fun createStrategy(onAssistantMessage: suspend (String) -> String) =
        functionalStrategy<String, String> { initialInput ->
            var inputMessage = initialInput
            var assistantMessage = ""

            while (inputMessage.isNotEmpty()) {
                var response = requestLLM(inputMessage)

                while (getToolCalls(response).isNotEmpty()) {
                    val results = executeTools(response)
                    response = sendToolResults(results)
                }

                assistantMessage = response.textContent()
                inputMessage = onAssistantMessage(assistantMessage)
            }
            assistantMessage
        }
}
