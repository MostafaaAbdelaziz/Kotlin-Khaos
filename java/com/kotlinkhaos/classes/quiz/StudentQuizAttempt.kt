package com.kotlinkhaos.classes.quiz

import com.google.firebase.FirebaseNetworkException
import com.kotlinkhaos.classes.errors.StudentQuizNetworkError
import com.kotlinkhaos.classes.services.KotlinKhaosQuizStudentApi
import com.kotlinkhaos.classes.services.StudentQuizAttemptRes
import com.kotlinkhaos.classes.services.StudentQuizsForCourseRes
import com.kotlinkhaos.classes.services.StudentWeeklySummaryRes
import com.kotlinkhaos.classes.user.User

/**
 * Represents an attempt by a student to take a quiz.
 */
class StudentQuizAttempt private constructor(
    private val id: String,
    private val quizName: String,
    private val questions: List<String>,
    private val userAnswers: MutableList<String>,
    private var finalScore: Int?,
) {
    companion object {
        /**
         * Creates a new quiz attempt for a given quiz.
         *
         * @param quizId The unique identifier of the quiz.
         * @return An instance of StudentQuizAttempt.
         * @throws StudentQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun createQuizAttempt(quizId: String): StudentQuizAttempt {
            try {
                val token = User.getJwt()
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(token)
                val res = kotlinKhaosApi.createStudentQuizAttempt(quizId)
                val questions = res.questions
                return StudentQuizAttempt(
                    res.quizAttemptId,
                    res.quizName,
                    questions,
                    userAnswers = emptyList<String>().toMutableList(),
                    finalScore = null
                )
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }

        /**
         * Retrieves details of a specific quiz attempt by its ID.
         *
         * @param quizAttemptId The unique identifier of the quiz attempt.
         * @return Details of the quiz attempt.
         * @throws StudentQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun getStudentQuizAttempt(quizAttemptId: String): StudentQuizAttemptRes.QuizAttempt {
            try {
                val token = User.getJwt()
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(token)
                return kotlinKhaosApi.getStudentQuizAttempt(quizAttemptId).quizAttempt
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }

        /**
         * Retrieves all quizzes available for a student's course.
         *
         * @return A list of quizzes available for the student's course.
         * @throws StudentQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun getQuizsForCourse(): List<StudentQuizsForCourseRes.StudentQuizDetailsRes> {
            try {
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(User.getJwt())
                return kotlinKhaosApi.getCourseQuizsForStudent().quizs
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }

        /**
         * Retrieves a weekly summary of quiz attempts and scores for a student.
         *
         * @return A weekly summary of the student's quiz activities.
         * @throws StudentQuizNetworkError On network issues.
         * @throws Exception On other errors.
         */
        suspend fun getWeeklySummaryForStudent(): StudentWeeklySummaryRes.WeeklySummary {
            try {
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(User.getJwt())
                return kotlinKhaosApi.getWeeklySummaryForStudent().weeklySummary
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }
    }

    /**
     * Submits the current quiz attempt with the student's answers to the backend and updates the final score.
     *
     * @throws StudentQuizNetworkError On network issues.
     * @throws Exception On other errors.
     */
    suspend fun submitAttempt() {
        try {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizStudentApi(token)
            val res =
                kotlinKhaosApi.submitStudentQuizAttempt(this.getQuizId(), this.getUserAnswers())
            setFinalScore(res.score)
        } catch (err: Exception) {
            if (err is FirebaseNetworkException) {
                throw StudentQuizNetworkError()
            }
            throw err
        }
    }

    fun getQuizId(): String {
        return this.id;
    }

    fun getQuizName(): String {
        return this.quizName;
    }

    fun isFinished(): Boolean {
        return getNumberOfQuestions() == getUserAnswers().size
    }

    private fun getNumberOfQuestions(): Int {
        return this.questions.size;
    }

    fun addUserAnswer(userAnswer: String) {
        if (!isFinished()) {
            userAnswers.add(userAnswer)
        }
    }

    private fun getUserAnswers(): List<String> {
        return this.userAnswers
    }

    fun getCurrentQuestionNumber(): Int {
        return getUserAnswers().size + 1;
    }

    fun getCurrentQuestion(): String {
        return this.questions[getUserAnswers().size];
    }

    private fun setFinalScore(finalScore: Int) {
        this.finalScore = finalScore
    }

    fun getFinalScore(): Int? {
        return this.finalScore
    }
}