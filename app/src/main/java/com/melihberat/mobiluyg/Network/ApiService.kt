package com.melihberat.mobiluyg.Network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Model sınıfları
import com.melihberat.mobiluyg.Network.model.LoginRequest
import com.melihberat.mobiluyg.Network.model.RegisterRequest
import com.melihberat.mobiluyg.Network.model.AuthResponse
import com.melihberat.mobiluyg.Network.model.UserDto
import com.melihberat.mobiluyg.Network.model.SchoolDto
import com.melihberat.mobiluyg.Network.model.DepartmentDto
import com.melihberat.mobiluyg.Network.model.LessonDto
import com.melihberat.mobiluyg.Network.model.QuestionDto
import com.melihberat.mobiluyg.Network.model.FavoriteDto
import com.melihberat.mobiluyg.Network.model.ApiMessage
import com.melihberat.mobiluyg.Network.model.FeedbackRequest
import com.melihberat.mobiluyg.Network.model.FeedbackItem

interface ApiService {
    // ---------- Auth ----------
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): UserDto

    // ---------- Public ----------
    @GET("schools")
    suspend fun getSchools(): List<SchoolDto>

    @GET("schools/{id}/departments")
    suspend fun getDepartmentsBySchool(@Path("id") schoolId: Int): List<DepartmentDto>

    @GET("departments/{id}/courses")
    suspend fun getCoursesByDepartment(@Path("id") departmentId: Int): List<LessonDto>

    // ---------- Questions ----------
    @GET("questions")
    suspend fun getQuestions(@Query("lessonId") lessonId: String): List<QuestionDto>

    // ---------- Favorites ----------
    @GET("favorites")
    suspend fun getFavorites(): List<FavoriteDto>

    @POST("favorites")
    suspend fun addFavorite(@Body body: Map<String, String>): ApiMessage

    @DELETE("favorites/{id}")
    suspend fun removeFavorite(@Path("id") id: String): ApiMessage

    // ---------- Feedback ----------
    @POST("feedback")
    suspend fun sendFeedback(@Body body: FeedbackRequest): ApiMessage

    @GET("feedbacks")
    suspend fun getFeedbacks(): List<FeedbackItem>

    // ---------- Account ----------
    @DELETE("users/me")
    suspend fun deleteMe(): ApiMessage
}

