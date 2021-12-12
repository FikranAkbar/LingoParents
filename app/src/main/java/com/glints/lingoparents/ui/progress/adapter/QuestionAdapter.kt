package com.glints.lingoparents.ui.progress.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.data.model.QuestionItem
import com.glints.lingoparents.databinding.ItemQuestionBinding

class QuestionAdapter : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
    private val questionItems = ArrayList<QuestionItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<QuestionItem>) {
        questionItems.clear()
        questionItems.addAll(list)
        notifyDataSetChanged()
    }

    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(question: QuestionItem)
    }

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(question: QuestionItem) {
            with(binding) {
                tvQuestion.text = question.questionNumber
                tvStudentAnswer.text = question.studentAnswer
                tvCorrectAnswer.text = question.correctAnswer
                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(question)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding =
            ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionItems[position])
    }

    override fun getItemCount(): Int = questionItems.size
}