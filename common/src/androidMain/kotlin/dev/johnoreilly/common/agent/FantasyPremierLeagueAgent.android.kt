package dev.johnoreilly.common.agent

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import dev.johnoreilly.common.BuildKonfig

actual fun getLLModel() = GoogleModels.Gemini2_5Flash

actual fun getPromptExecutor(): PromptExecutor {
    // Pass the factory explicitly, as iOS does. The JVM-and-Android convenience overload resolves
    // it through ServiceLoader, but Koog's Android artifact ships the KtorKoogHttpClient.Factory
    // class without the META-INF/services entry that registers it, so that lookup finds nothing.
    return simpleGoogleAIExecutor(BuildKonfig.GEMINI_API_KEY, KtorKoogHttpClient.Factory())
}
