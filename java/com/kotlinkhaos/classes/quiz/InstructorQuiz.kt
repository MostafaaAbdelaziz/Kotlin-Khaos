package com.kotlinkhaos.classes.quiz

import com.google.firebase.FirebaseNetworkException
import com.kotlinkhaos.classes.errors.InstructorQuizCreationError
import com.kotlinkhaos.classes.errors.InstructorQuizNetworkError
import com.kotlinkhaos.classes.services.InstructorQuizCreateReq
import com.kotlinkhaos.classes.services.InstructorQuizsForCourseRes
import com.kotlinkhaos.classes.services.KotlinKhaosQuizInstructorApi
import com.kotlinkhaos.classes.user.User

/**
 * Represents a quiz from the perspective of an instructor, allowing for quiz creation, modification, and retrieval.
 */
class InstructorQuiz private constructor(
    private val id: String,
    private var name: String,
    private val questionLimit: Int,
    private var questions: MutableList<String>
) {
    companion object {
        /**
         * Validates quiz creation options.
         *
         * @param quizCreateOptions The options for creating a quiz.
         * @throws InstructorQuizCreationError If name or prompt is empty.
         */
        private fun validateQuizCreateOptions(quizCreateOptions: InstructorQuizCreateReq.Options) {
            if (quizCreateOptions.name.isEmpty() || quizCreateOptions.prompt.isEmpty()) {
                throw InstructorQuizCreationError("Name and prompt must not be empty")
            }
        }

        /**
         * Creates a new quiz with specified options.
         *
         * @param quizCreateOptions The options for creating the quiz.
         * @return An instance of InstructorQuiz.
         * @throws InstructorQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun createQuiz(
            quizCreateOptions: InstructorQuizCreateReq.Options
        ): InstructorQuiz {
            try {
                validateQuizCreateOptions(quizCreateOptions)
                val token = User.getJwt()
                val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
                val res = kotlinKhaosApi.createQuiz(quizCreateOptions)
                val questions = mutableListOf(res.firstQuestion)
                return InstructorQuiz(
                    res.quizId,
                    quizCreateOptions.name,
                    quizCreateOptions.questionLimit,
                    questions
                )
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw InstructorQuizNetworkError()
                }
                throw err
            }
        }

        /**
         * Retrieves all quizzes for a course.
         *
         * @return A list of InstructorQuizDetailsRes containing quiz details.
         * @throws InstructorQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun getQuizsForCourse(): List<InstructorQuizsForCourseRes.InstructorQuizDetailsRes> {
            try {
                val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(User.getJwt())
                return kotlinKhaosApi.getCourseQuizsForInstructor().quizs
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw InstructorQuizNetworkError()
                }
                throw err
            }
        }

        /**
         * Finishes a quiz with the given quiz ID.
         *
         * @param quizId The unique identifier of the quiz to be finished.
         * @throws InstructorQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun finish(quizId: String) {
            try {
                val token = User.getJwt()
                val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
                kotlinKhaosApi.finishQuiz(quizId)
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw InstructorQuizNetworkError()
                }
                throw err
            }
        }
    }

    fun getQuizId(): String {
        return this.id;
    }

    fun getName(): String {
        return this.name;
    }

    fun getQuestionLimit(): Int {
        return this.questionLimit;
    }

    private fun setQuestions(questions: List<String>) {
        this.questions = questions.toMutableList()
    }

    fun getQuestions(): List<String> {
        return this.questions;
    }

    private fun appendQuestion(question: String) {
        this.questions.add(question)
    }

    suspend fun nextQuestion() {
        try {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
            val res = kotlinKhaosApi.nextQuestion(this.getQuizId())
            appendQuestion(res.question)
        } catch (err: Exception) {
            if (err is FirebaseNetworkException) {
                throw InstructorQuizNetworkError()
            }
            throw err
        }
    }

    suspend fun editQuestions(questions: List<String>) {
        try {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
            kotlinKhaosApi.editQuestions(this.getQuizId(), questions)
            setQuestions(questions)
        } catch (err: Exception) {
            if (err is FirebaseNetworkException) {
                throw InstructorQuizNetworkError()
            }
            throw err
        }
    }

    suspend fun start() {
        try {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
            kotlinKhaosApi.startQuiz(this.getQuizId())
        } catch (err: Exception) {
            if (err is FirebaseNetworkException) {
                throw InstructorQuizNetworkError()
            }
            throw err
        }
    }
}