package dev.johnoreilly.climatetrace.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.asAssistantMessage
import ai.koog.agents.core.agent.containsToolCalls
import ai.koog.agents.core.agent.executeMultipleTools
import ai.koog.agents.core.agent.extractToolCalls
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.agent.requestLLMMultiple
import ai.koog.agents.core.agent.sendMultipleToolResults
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository


expect fun getLLModel(): LLModel
expect fun getPromptExecutor(apiKey: String = ""): PromptExecutor

class FantasyPremierLeagueAgent(private val fantasyPremierLeagueRepository: FantasyPremierLeagueRepository) {
    private val apiKeyGoogle = ""

    suspend fun createAgent() = AIAgent<String, String>(
            promptExecutor = getPromptExecutor(apiKeyGoogle),
            llmModel = getLLModel(),
            toolRegistry = createToolSetRegistry(fantasyPremierLeagueRepository),
            strategy = functionalStrategy { input ->
                println("Calling LLM with Input = $input")
                var responses = requestLLMMultiple(input)

                while (responses.containsToolCalls()) {
                    val pendingCalls = extractToolCalls(responses)
                    println("Pending Calls")
                    println(pendingCalls.map { "${it.tool} ${it.content}" })
                    val results = executeMultipleTools(pendingCalls, parallelTools = true)
                    responses = sendMultipleToolResults(results)
                }

                responses.single().asAssistantMessage().content
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
