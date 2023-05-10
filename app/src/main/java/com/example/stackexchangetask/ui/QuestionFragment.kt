package com.example.stackexchangetask.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.stackexchangetask.QuestionViewModel
import com.example.stackexchangetask.R
import com.example.stackexchangetask.adapters.QuestionAdapter
import com.example.stackexchangetask.model.Question
import com.example.stackexchangetask.utils.Resource
import com.example.stackexchangetask.utils.openQuestion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private val viewModel: QuestionViewModel by viewModels()
    private var job: Job? = null
    private lateinit var questionsAdapter: QuestionAdapter
    private lateinit var searchedQuestionsAdapter: QuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        viewModel.questions.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    questions_rv.isGone = false
                    questionsAdapter.submitList(resource.data!!.items)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()

                }

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.searchedQuestions.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    progress_bar.isGone = true
                    if (resource.data!!.items.isNotEmpty()) {
                        searchedQuestionsAdapter.submitList(resource.data.items)
                        questions_searched_rv.isGone = false
                        null_search.isGone = true
                    } else {
                        null_search.isGone = false
                        questions_searched_rv.isGone = true
                    }
                }
                else -> {}
            }
        }

        searchET?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                if (it.trim().isNotBlank() && it.trim().isNotEmpty()) {
                    clearQuery.isGone = false
                    searchIcon.isVisible = false
                    questions_rv.isGone = true

                    search_key_text.text = "Searched questions"
                    progress_bar.isGone = false
                    null_search.isGone = true
                    error_network.isGone = true
                    questions_searched_rv.isGone = true
                    searchQuestion(it.trim().toString())
                } else {
                    clearQuery.isGone = true
                    searchIcon.isVisible = true
                    search_key_text.text = "Questions"
                    questions_rv.isGone = false
                    questions_searched_rv.isGone = true
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        questions_rv.setHasFixedSize(true)
        questionsAdapter = QuestionAdapter(object : QuestionAdapter.OnClickListener {
            override fun openQuestion(questionItem: Question) {
                openQuestion(questionItem, requireContext())
            }
        }, requireContext())
        questions_rv.adapter = questionsAdapter

    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun searchQuestion(query: String) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(2000)
            viewModel.searchQuestions(query)
        }
    }

}