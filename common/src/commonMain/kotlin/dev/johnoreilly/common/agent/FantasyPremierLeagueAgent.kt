package dev.johnoreilly.climatetrace.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import dev.johnoreilly.common.agent.GetFixturesTool
import dev.johnoreilly.common.agent.GetPlayersTool
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository


expect fun getLLModel(): LLModel
expect fun getPromptExecutor(apiKey: String = ""): PromptExecutor

class FantasyPremierLeagueAgent(private val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) {
    private val apiKeyGoogle = ""

    suspend fun createAgent() = AIAgent(
            promptExecutor = getPromptExecutor(apiKeyGoogle),
            llmModel = getLLModel(),
            toolRegistry = createToolSetRegistry(fantasyPremierLeagueRepository),
            strategy = functionalStrategy<String, String> { input ->
                println("Calling LLM with Input = $input")
                var response = requestLLM(input)

                var toolCalls = getToolCalls(response)
                while (toolCalls.isNotEmpty()) {
                    println("Pending Calls")
                    println(toolCalls.map { "${it.tool} ${it.args}" })
                    val results = executeTools(toolCalls, parallelTools = true)
                    response = sendToolResults(results)
                    toolCalls = getToolCalls(response)
                }

                getTextParts(response).joinToString("") { it.text }
            },
        systemPrompt  =
            """
                You an AI assistant specialising in providing information about the fantasy premier league competition.
                The current date is October 19th 2025.

                Only use the tools provided to get player and fixture data.
            """.trimIndent(),
        )



    suspend fun runAgent(prompt: String): String {
        val agent = createAgent()
        println("Running agent")
        val output = agent.run(prompt)
        println("Result = $output")
        return output
    }

    private suspend fun createToolSetRegistry(fantasyPremierLeagueRepository: FantasyPremierLeagueRepository): ToolRegistry {
        val localToolSetRegistry = ToolRegistry {
            tool(GetPlayersTool(fantasyPremierLeagueRepository))
            tool(GetFixturesTool(fantasyPremierLeagueRepository))
        }
        return localToolSetRegistry
    }
}
