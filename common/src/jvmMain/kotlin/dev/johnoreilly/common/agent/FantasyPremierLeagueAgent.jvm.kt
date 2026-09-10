package dev.johnoreilly.common.agent

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import dev.johnoreilly.common.BuildKonfig



actual fun getLLModel() = GoogleModels.Gemini2_5Flash

actual fun getPromptExecutor(): PromptExecutor {
    return simpleGoogleAIExecutor(BuildKonfig.GEMINI_API_KEY)
}
//
//actual fun getLLModel() = LLModel(
//    provider = LLMProvider.Ollama,
//    id = "gemma4:26b-mlx",
//    capabilities = listOf(
//        LLMCapability.Temperature,
//        LLMCapability.Schema.JSON.Standard,
//        LLMCapability.Tools
//    ),
//    contextLength = 2048,
//)
//
//actual fun getPromptExecutor(): PromptExecutor {
//    return simpleOllamaAIExecutor()
//}

