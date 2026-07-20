package dev.johnoreilly.climatetrace.agent

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor

actual fun getLLModel() = GoogleModels.Gemini2_5Flash

actual fun getPromptExecutor(apiKey: String): PromptExecutor {
    return simpleGoogleAIExecutor(apiKey, KtorKoogHttpClient.Factory())
}
