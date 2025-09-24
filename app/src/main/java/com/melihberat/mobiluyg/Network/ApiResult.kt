package com.melihberat.mobiluyg.Network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val message: String, val code: Int? = null): ApiResult<Nothing>()
}

suspend fun <T> safeCall(block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: retrofit2.HttpException) {
        ApiResult.Error(e.response()?.errorBody()?.string() ?: e.message(), e.code())
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Bilinmeyen hata")
    }
}

