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
import androidx.navigation.fragment.findNavController
import com.example.stackexchangetask.QuestionViewModel
import com.example.stackexchangetask.R
import com.example.stackexchangetask.adapters.QuestionAdapter
import com.example.stackexchangetask.broadcast.NetworkError
import com.example.stackexchangetask.databinding.FragmentQuestionBinding
import com.example.stackexchangetask.databinding.FragmentSearchBinding
import com.example.stackexchangetask.model.Question
import com.example.stackexchangetask.utils.Resource
import com.example.stackexchangetask.utils.openQuestion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding
    private val viewModel: QuestionViewModel by viewModels()
    private var job: Job? = null
    private lateinit var questionsAdapter: QuestionAdapter
    private lateinit var searchedQuestionsAdapter: QuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentQuestionBinding.bind(view)
        _binding = binding

        NetworkError(requireContext()).observe(viewLifecycleOwner) {
            if (!it) {
                //No Internet connection
                findNavController().navigate(R.id.action_questionFragment_to_noInternet)

            } else {

            }
        }

        setUpRecyclerView()
        binding.clearQuery.setOnClickListener {
            binding.searchET.text.clear()
            hideKeyboard()
        }

        binding.filterIcon.setOnClickListener {
            findNavController().navigate(R.id.action_questionFragment_to_searchFragment)
        }

        viewModel.questions.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.questionsRv.isGone = false
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
                    binding.progressBar.isGone = true
                    if (resource.data!!.items.isNotEmpty()) {
                        searchedQuestionsAdapter.submitList(resource.data.items)
                        binding.questionsSearchedRv.isGone = false
                        binding.nullSearch.isGone = true
                    } else {
                        binding.nullSearch.isGone = false
                        binding.questionsSearchedRv.isGone = true
                    }
                }
                else -> {}
            }
        }

        binding.searchET?.doOnTextChanged { text, _, _, _ ->
            text?.let {
                if (it.trim().isNotBlank() && it.trim().isNotEmpty()) {
                    binding.clearQuery.isGone = false
                    binding.searchIcon.isVisible = false
                    binding.questionsRv.isGone = true

                    binding.searchKeyText.text = "Searched questions"
                    binding.progressBar.isGone = false
                    binding.nullSearch.isGone = true
                    binding.errorNetwork.isGone = true
                    binding.questionsSearchedRv.isGone = true
                    searchQuestion(it.trim().toString())
                } else {
                    binding.clearQuery.isGone = true
                    binding.searchIcon.isVisible = true
                    binding.searchKeyText.text = "Questions"
                    binding.questionsRv.isGone = false
                    binding.questionsSearchedRv.isGone = true
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding?.questionsRv?.setHasFixedSize(true)
        questionsAdapter = QuestionAdapter(object : QuestionAdapter.OnClickListener {
            override fun openQuestion(questionItem: Question) {
                openQuestion(questionItem, requireContext())
            }
        }, requireContext())
        binding?.questionsRv?.adapter = questionsAdapter

        binding?.questionsSearchedRv?.setHasFixedSize(true)
        searchedQuestionsAdapter = QuestionAdapter(object : QuestionAdapter.OnClickListener {
            override fun openQuestion(questionItem: Question) {
                openQuestion(questionItem, requireContext())
            }
        }, requireContext())
        binding?.questionsSearchedRv?.adapter = searchedQuestionsAdapter

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