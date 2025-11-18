package com.webmd.newsfeed.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

sealed class AppNetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : AppNetworkResult<T>(data)
    class Failed<T>(data: T?, message: String?) : AppNetworkResult<T>(data, message)
    class Loading<T>(data: T?) : AppNetworkResult<T>()
}

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()
    val flow = if (shouldFetch(data)) {
        emit(AppNetworkResult.Loading(data))
        try {
            saveFetchResult(fetch())
            query().map { AppNetworkResult.Success(it) }
        } catch (e: HttpException) {
            query().map {
                AppNetworkResult.Failed(
                    it, e.localizedMessage ?: "An unexpected error occurred"
                )
            }
        } catch (_: IOException) {
            query().map {
                AppNetworkResult.Failed(
                    it, "Failed to get Headlines, Please check your Internet connection"
                )
            }
        }
    } else {
        query().map { AppNetworkResult.Success(it) }
    }
    emitAll(flow)
}