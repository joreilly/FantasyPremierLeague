package dev.johnoreilly.common.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.functionalStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.model.StructureFixingParser
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository

class FantasyPremierLeagueAgentProvider(
    private val repository: FantasyPremierLeagueRepository
) : AgentProvider {

    override val description: String =
        "Hi, I'm a Fantasy Premier League assistant. Ask me about players, fixtures and teams."

    override suspend fun provideAgent(
        onToolCallEvent: suspend (String) -> Unit,
        onErrorEvent: suspend (String) -> Unit,
        onAssistantMessage: suspend (FplAnswer) -> String,
    ): AIAgent<String, String> {

        val toolRegistry = ToolRegistry {
            tool(GetPlayersTool(repository))
            tool(GetFixturesTool(repository))
            tool(GetLeagueStandingsTool(repository))
            tool(BuildSquadTool(repository))
        }

        val agentConfig = AIAgentConfig(
            prompt = prompt("fantasyPremierLeague") {
                system(
                    """
                    You are an AI assistant specialising in the Fantasy Premier League competition.
                    Only use the tools provided to get player, fixture and mini-league standings data.
                    Keep answers concise and, where useful, present players or fixtures as a short list.

                    For squad-building requests (e.g. "pick my team", "build me a squad"), call the
                    buildSquad tool rather than selecting players yourself - it runs a real optimiser
                    under budget, position and club-limit constraints, which you cannot reliably do by
                    reasoning over the player list. Pass lookaheadGameweeks when the user asks for a
                    squad geared toward upcoming/opening fixtures; omit it for a season-long best squad.
                    The tool also returns the best starting XI, formation and captain/vice-captain for
                    the next gameweek - mention these when the user asks about a starting lineup or
                    captain, not just the full 15-man squad. Put every player id you reference (full
                    squad or just the starting XI) in playerIds so they render as cards.
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
     * feeding results back to the LLM until it has an answer. The answer is then requested
     * as structured [FplAnswer] data so the UI gets the exact player ids to render as cards.
     */
    private fun createStrategy(onAssistantMessage: suspend (FplAnswer) -> String) =
        functionalStrategy<String, String> { initialInput ->
            var inputMessage = initialInput
            var lastText = ""

            while (inputMessage.isNotEmpty()) {
                var response = requestLLM(inputMessage)

                while (getToolCalls(response).isNotEmpty()) {
                    val results = executeTools(response)
                    response = sendToolResults(results)
                }

                // Ask the model to express its answer as structured data (deterministic player ids)
                val answer = requestLLMStructured<FplAnswer>(
                    "Return your final answer as structured data. Put the FPL 'id' of every player you " +
                        "referenced in 'playerIds' and every fixture in 'fixtureIds'. Those players/fixtures are " +
                        "shown to the user as cards, so 'text' should be a brief markdown lead-in or summary and " +
                        "must NOT re-list the individual players or fixtures. If there are no ids, put the full " +
                        "answer in 'text'.",
                    fixingParser = StructureFixingParser(getLLModel(), retries = 2),
                ).getOrNull()?.data ?: FplAnswer(text = response.textContent())

                lastText = answer.text
                inputMessage = onAssistantMessage(answer)
            }
            lastText
        }
}
