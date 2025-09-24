package com.melihberat.mobiluyg.Network

import com.melihberat.mobiluyg.Network.model.*
import retrofit2.http.*

interface ApiService {

    // ---------- Auth ----------
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("/api/auth/me")
    suspend fun me(): UserDto

    // ---------- Public: Schools / Departments / Courses ----------
    @GET("/api/schools")
    suspend fun getSchools(): List<SchoolDto>

    @GET("/api/schools/{id}/departments")
    suspend fun getDepartmentsBySchool(@Path("id") schoolId: Int): List<DepartmentDto>

    // Backend burada "courses" döndürüyor; biz bunları app tarafında LessonDto olarak kullanıyoruz
    @GET("/api/departments/{id}/courses")
    suspend fun getCoursesByDepartment(@Path("id") departmentId: Int): List<LessonDto>

    // ---------- Questions ----------
    // Not: Şu an LessonActivity getQuestions(lessonId=lessonTitle) çağırıyor.
    // Backend'te query ismi farklıysa ileride uyarlayabiliriz.
    @GET("/api/questions")
    suspend fun getQuestions(@Query("lessonId") lessonId: String): List<QuestionDto>

    // ---------- Favorites (backend’de varsa kullanılır) ----------
    @GET("/api/favorites")
    suspend fun getFavorites(): List<FavoriteDto>

    @POST("/api/favorites")
    suspend fun addFavorite(@Body body: Map<String, String>): ApiMessage

    @DELETE("/api/favorites/{id}")
    suspend fun removeFavorite(@Path("id") id: String): ApiMessage

    // ---------- Feedback ----------
    @POST("/api/feedback")
    suspend fun sendFeedback(@Body body: FeedbackRequest): ApiMessage

    @GET("/api/feedbacks")
    suspend fun getFeedbacks(): List<FeedbackItem>
}

