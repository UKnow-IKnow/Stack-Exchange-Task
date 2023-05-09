package com.example.stackexchangetask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.example.stackexchangetask.QuestionViewModel
import com.example.stackexchangetask.R
import com.example.stackexchangetask.adapters.QuestionAdapter
import com.example.stackexchangetask.model.Question
import com.example.stackexchangetask.utils.Resource
import com.example.stackexchangetask.utils.openQuestion
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.coroutines.Job

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private val viewModel: QuestionViewModel by viewModels()
    private var job: Job? = null
    private lateinit var questionsAdapter: QuestionAdapter

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



}