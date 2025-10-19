package dev.johnoreilly.climatetrace.agent

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

//actual fun getLLModel(): LLModel {
//    return LLModel(
//        provider = LLMProvider.Ollama,
//        id = "gpt-oss:20b",
//        capabilities = listOf(
//            LLMCapability.Temperature,
//            LLMCapability.Schema.JSON.Standard,
//            LLMCapability.Tools
//        ),
//        contextLength = 128_000,
//    )
//}
//
//actual fun getPromptExecutor(apiKey: String): PromptExecutor {
//    return simpleOllamaAIExecutor()
//}

actual fun getLLModel() = GoogleModels.Gemini2_5Pro

actual fun getPromptExecutor(apiKey: String): PromptExecutor {
    return simpleGoogleAIExecutor(apiKey)
}