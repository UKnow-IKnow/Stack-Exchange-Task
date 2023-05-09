package com.example.stackexchangetask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stackexchangetask.model.QuestionResponse
import com.example.stackexchangetask.repository.QuestionRepository
import com.example.stackexchangetask.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject internal constructor(private val repository: QuestionRepository) : ViewModel() {

    private val _questions = MutableLiveData<Resource<QuestionResponse>>()
    val questions: LiveData<Resource<QuestionResponse>> get() = _questions

    init {
        getQuestions()
    }

    fun getQuestions() = viewModelScope.launch {
        _questions.postValue(Resource.Loading())

        val response = repository.getQuestions()
        _questions.postValue(safeHandleResponse(response))


    }

    private suspend fun safeHandleResponse(response: Response<QuestionResponse>): Resource<QuestionResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(data = response.body()!!)
            } catch (e: Exception) {
                Resource.Error(e.message.toString())
            }
        }
    }
}