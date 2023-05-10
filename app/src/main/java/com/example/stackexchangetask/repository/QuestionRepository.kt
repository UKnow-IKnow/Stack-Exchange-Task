package com.example.stackexchangetask.repository

import com.example.stackexchangetask.api.StackExchangeApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val stackExchangeApi: StackExchangeApi) {

    suspend fun getQuestions() = stackExchangeApi.getQuestions()

    suspend fun searchQuestions(query: String) = stackExchangeApi.searchQuestions(query = query)

    suspend fun searchWithTag(tags: String) = stackExchangeApi.searchWithTag(tags = tags)


}