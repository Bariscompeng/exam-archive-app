package com.melihberat.mobiluyg.Network.model

data class FavoriteDto(
    val id: String? = null,
    val lessonId: String? = null,
    val text: String? = null,
    val options: List<String> = emptyList()
)

