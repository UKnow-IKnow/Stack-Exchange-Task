package com.example.stackexchangetask.api

import com.example.stackexchangetask.model.QuestionResponse
import com.example.stackexchangetask.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StackExchangeApi {

    @GET("2.2/questions")
    suspend fun getQuestions(
        @Query("key")
        key: String = API_KEY,
        @Query("order")
        order: String = "desc",
        @Query("sort")
        sort: String = "activity",
        @Query("site")
        site: String = "stackoverflow",
        @Query("page")
        page: Int = 1,
        @Query("pageSize")
        pageSize: Int = 25
    ): Response<QuestionResponse>

    @GET("/2.3/search")
    suspend fun searchQuestions(
        @Query("key")
        key: String = API_KEY,
        @Query("intitle")
        query: String,
        @Query("order")
        order: String = "desc",
        @Query("sort")
        sort: String = "votes",
        @Query("site")
        site: String = "stackoverflow",
        @Query("page")
        page: Int = 1,
        @Query("pageSize")
        pageSize: Int = 25
    ): Response<QuestionResponse>

    @GET("/2.3/search")
    suspend fun searchWithTag(
        @Query("key")
        key: String = API_KEY,
        @Query("tagged")
        tags: String,
        @Query("order")
        order: String = "desc",
        @Query("sort")
        sort: String = "votes",
        @Query("site")
        site: String = "stackoverflow",
        @Query("page")
        page: Int = 1,
        @Query("pageSize")
        pageSize: Int = 25
    ): Response<QuestionResponse>

}