package com.glints.lingoparents.ui.progress.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glints.lingoparents.data.model.SessionItem
import com.glints.lingoparents.data.model.response.CourseDetailByStudentIdResponse
import com.glints.lingoparents.databinding.ItemSessionBinding

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private val sessionItems = ArrayList<CourseDetailByStudentIdResponse.SessionsItem>()

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<CourseDetailByStudentIdResponse.SessionsItem>) {
        sessionItems.clear()
        sessionItems.addAll(list)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(session: CourseDetailByStudentIdResponse.SessionsItem)
    }

    inner class SessionViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: CourseDetailByStudentIdResponse.SessionsItem) {
            with(binding) {

                tvSession.text = session.sessionTitle
                tvShortdesc.text = ""
                tvSessiondesc.text = session.feedback
                tvSessionscore.text = session.score.toString()

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(session)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessionItems[position])
    }

    override fun getItemCount(): Int = sessionItems.size

}