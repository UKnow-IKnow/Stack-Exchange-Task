package com.example.stackexchangetask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.example.stackexchangetask.QuestionViewModel
import com.example.stackexchangetask.R
import com.example.stackexchangetask.adapters.QuestionAdapter
import com.example.stackexchangetask.databinding.FragmentSearchBinding
import com.example.stackexchangetask.model.Question
import com.example.stackexchangetask.utils.Resource
import com.example.stackexchangetask.utils.openQuestion
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding
    private val viewModel by activityViewModels<QuestionViewModel>()
    private lateinit var tagAdapter: QuestionAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
        _binding = binding

        setUpRecyclerView()

        binding.closeTags.setOnClickListener {
            dismiss()
            tagAdapter.submitList(null)
        }

        binding.useTagsButton.setOnClickListener {
            binding.tagsInputText.editText?.text?.toString()?.trim()?.let { tags ->
                binding.tagsInputText.error = null
                viewModel.searchWithTag(tags)
            } ?: kotlin.run { binding.tagsInputText.error = "Cannot be empty" }
        }

        viewModel.taggedQuestions.observe(viewLifecycleOwner) { resource ->
            when (resource) {

                is Resource.Success -> binding?.apply {
                    useTagsButton.text = "Use Tags"
                    searchingTagsProgressbar.isGone = true

                    if (resource.data!!.items.isNotEmpty()) {
                        tagAdapter.submitList(resource.data.items)
                        questionsAfterTagRv.isGone = false
                        nullSearchTags.isGone = true
                    } else {
                        nullSearchTags.isGone = false
                        questionsAfterTagRv.isGone = true
                    }
                }

                is Resource.Error -> binding?.apply {
                    searchingTagsProgressbar.isGone = true
                    errorNetworkTags.isGone = false
                    useTagsButton.text = "Use Tags"
                    Toast.makeText(requireContext(), "Error!!!", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> binding?.apply {
                    searchingTagsProgressbar.isGone = false
                    errorNetworkTags.isGone = true
                    useTagsButton.text = "Loading"
                    nullSearchTags.isGone = true
                    questionsAfterTagRv.isGone = true
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding?.apply {
            questionsAfterTagRv.setHasFixedSize(true)
            tagAdapter = QuestionAdapter(object : QuestionAdapter.OnClickListener {
                override fun openQuestion(questionItem: Question) {
                    openQuestion(questionItem, requireContext())
                }
            }, requireContext())
            questionsAfterTagRv.adapter = tagAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}