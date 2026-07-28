package dev.johnoreilly.common.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository


/**
 * One-shot console agent used by the JVM `main` demo. The UI uses
 * [FantasyPremierLeagueAgentProvider] instead (streaming, multi-turn).
 */
class FantasyPremierLeagueAgent(private val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) {

    suspend fun createAgent() = AIAgent(
        promptExecutor = getPromptExecutor(),
        llmModel = getLLModel(),
        toolRegistry = createToolSetRegistry(fantasyPremierLeagueRepository),
        strategy = functionalStrategy<String, String> { input ->
            var response = requestLLM(input)
            var toolCalls = getToolCalls(response)
            while (toolCalls.isNotEmpty()) {
                val results = executeTools(response)
                response = sendToolResults(results)
                toolCalls = getToolCalls(response)
            }
            getTextParts(response).joinToString("") { it.text }
        },
        systemPrompt =
            """
                You an AI assistant specialising in providing information about the fantasy premier league competition.
                Only use the tools provided to get player and fixture data.
            """.trimIndent(),
    )

    suspend fun runAgent(prompt: String): String {
        val agent = createAgent()
        val output = agent.run(prompt)
        return output
    }

    private fun createToolSetRegistry(fantasyPremierLeagueRepository: FantasyPremierLeagueRepository): ToolRegistry {
        return ToolRegistry {
            tool(GetPlayersTool(fantasyPremierLeagueRepository))
            tool(GetFixturesTool(fantasyPremierLeagueRepository))
        }
    }
}
