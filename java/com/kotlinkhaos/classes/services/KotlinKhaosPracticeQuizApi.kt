package com.kotlinkhaos.classes.services;

import android.util.Log
import com.kotlinkhaos.classes.errors.KotlinKhaosApiError
import com.kotlinkhaos.classes.errors.PracticeQuizApiError
import com.kotlinkhaos.classes.errors.PracticeQuizNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException

/**
 * API client for Kotlin Khaos Practice Quiz.
 * Handles network operations for starting, getting, answering, and continuing practice quizzes.
 * Refer to https://kotlin-khaos-api.maximoguk.com/docs/
 *
 * @param token The authentication token required for API requests.
 */
class KotlinKhaosPracticeQuizApi(private val token: String) {
    private val apiHost = "https://kotlin-khaos-api.maximoguk.com"
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
    }

    /**
     * Starts a new practice quiz with a given prompt.
     */
    suspend fun startPracticeQuiz(prompt: String): PracticeQuizStartRes {
        try {
            val res = client.post("$apiHost/practice-quizs") {
                parameter("prompt", prompt)
                addRequiredApiHeaders()
            }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in startPracticeQuiz", err)
            if (err is UnresolvedAddressException || err is HttpRequestTimeoutException) {
                throw PracticeQuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    /**
     * Retrieves a practice quiz by its ID.
     */
    suspend fun getPracticeQuizById(
        practiceQuizId: String
    ): PracticeQuizGetByIdRes {
        try {
            val res =
                client.get("$apiHost/practice-quizs/$practiceQuizId") {
                }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in getPractice", err)
            if (err is UnresolvedAddressException || err is HttpRequestTimeoutException) {
                throw PracticeQuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    /**
     * Sends an answer for a specific practice quiz and gets feedback.
     */
    suspend fun sendPracticeQuizAnswer(
        practiceQuizId: String,
        answer: String
    ): PracticeQuizAnswerRes {
        try {
            val res =
                client.post("$apiHost/practice-quizs/$practiceQuizId") {
                    setBody(PracticeQuizAnswerReq(answer))
                    addRequiredApiHeaders()
                }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in sendPracticeQuizAnswer", err)
            if (err is UnresolvedAddressException || err is HttpRequestTimeoutException) {
                throw PracticeQuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    /**
     * Continue a practice quiz, which results in getting a new problem or finishing the quiz if
     * there are no more questions available.
     */
    suspend fun continuePracticeQuiz(
        practiceQuizId: String
    ): PracticeQuizContinueRes {
        try {
            val res =
                client.post("$apiHost/practice-quizs/$practiceQuizId/continue") {
                    addRequiredApiHeaders()
                }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in continuePracticeQuiz", err)
            if (err is UnresolvedAddressException || err is HttpRequestTimeoutException) {
                throw PracticeQuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    private fun HttpRequestBuilder.addRequiredApiHeaders() {
        header(HttpHeaders.Authorization, "Bearer $token")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    private suspend inline fun <reified T> parseResponseFromApi(res: HttpResponse): T {
        if (res.status.isSuccess()) {
            return res.body()
        }
        val apiError: KotlinKhaosApiError = res.body()
        throw PracticeQuizApiError(apiError.status, apiError.error)
    }
}

@Serializable
data class PracticeQuizStartRes(val problem: String, val practiceQuizId: String)

@Serializable
data class PracticeQuizGetByIdRes(val message: String? = null, val score: Int? = null)

@Serializable
data class PracticeQuizAnswerReq(val answer: String)

@Serializable
data class PracticeQuizAnswerRes(val feedback: String)

@Serializable
data class PracticeQuizContinueRes(val problem: String? = null, val score: Int? = null)