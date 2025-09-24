package com.melihberat.mobiluyg.Network.model

import com.google.gson.annotations.SerializedName

data class SchoolDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String
)

data class CategoryDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val title: String
)

data class LessonDto(
    @SerializedName("id") val id: Int?,
    // Backend JSON'da "name" var ama app tarafında "title" bekleniyor
    @SerializedName("name") val title: String,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("description") val description: String? = null
)

data class QuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("lessonId") val lessonId: Int,
    @SerializedName("text") val text: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("answerIndex") val answerIndex: Int
)

data class FeedbackRequest(
    @SerializedName("message") val message: String,
    @SerializedName("rating") val rating: Int
)

data class ApiMessage(
    @SerializedName("message") val message: String
)

