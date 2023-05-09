package com.example.stackexchangetask

import androidx.lifecycle.ViewModel
import com.example.stackexchangetask.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject internal constructor(private val repository: QuestionRepository) : ViewModel() {


}