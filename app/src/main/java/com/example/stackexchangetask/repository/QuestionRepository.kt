package com.example.stackexchangetask.repository

import com.example.stackexchangetask.api.StackExchangeApi

class QuestionRepository (private val stackExchangeApi: StackExchangeApi){

    suspend fun getQuestions() = stackExchangeApi.getQuestions()

    suspend fun searchQuestions(query: String) = stackExchangeApi.searchQuestions(query = query)

    suspend fun searchWithTag(tags: String) = stackExchangeApi.searchWithTag(tags = tags)


}