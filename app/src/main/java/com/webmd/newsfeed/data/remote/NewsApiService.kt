package com.webmd.newsfeed.data.remote

import com.webmd.newsfeed.data.model.NewsResponse
import com.webmd.newsfeed.utils.AppConstant
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Remote data source - API service interface
 * Returns DTOs (Data Transfer Objects) from the API
 */
interface NewsApiService {
    @GET(AppConstant.END_POINT)
    suspend fun getTopHeadlines(
        @Query("country") country: String = AppConstant.DEFAULT_COUNTRY,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}
